package com.acme.dbo.client.service;

import com.acme.dbo.client.dao.ClientDao;
import com.acme.dbo.client.dao.ClientDataDao;
import com.acme.dbo.client.dao.ClientDataTypeDao;
import com.acme.dbo.client.domain.Client;
import com.acme.dbo.client.domain.ClientData;
import com.acme.dbo.client.domain.ClientDataType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static java.time.Instant.now;

@Service
@Slf4j
public class ClientValidationService {
    @Autowired
    ClientDao clientDao;
    @Autowired
    ClientDataDao clientDataDao;
    @Autowired
    ClientDataTypeDao clientDataTypeDao;

    public boolean updateClientDataByClientId(Long clientId, String dataDescription, String dataValue){
        Client client = getClientById(clientId);
        if(client==null) return false;

        ClientDataType clientDataType = clientDataTypeDao.findByDescription(dataDescription).filter(it -> it != null).orElse(null);
        if (clientDataType == null) {
            log.info(String.format("client data type by \"%s\" null", dataDescription));
            return false;
        }

        ClientData clientData = clientDataDao.findByClientId(client.getId()).filter(it -> it != null).orElse(null);
        if (clientData != null && clientData.getTypeId().equals(clientDataType.getId()) && dataDescription.equals("create")) {
            clientDataDao.delete(clientData);
        }

        ClientData data = ClientData.builder()
            .postStamp(now())
            .expireStamp(now().plus(1, ChronoUnit.DAYS))
            .clientId(client.getId())
            .typeId(clientDataTypeDao.findByDescription(dataDescription).get().getId())
            .value(dataValue)
            .build();

        ClientData save = clientDataDao.save(data);

        return data.equals(save);
    }

    public boolean blockClient(Long clientId) {
        Optional<Client> clientById = clientDao.findById(clientId);
        return clientById.filter(this::blockClient).isPresent();
    }

    public boolean blockClient(final Client client) {
        ClientData clientData;

        if (clientIsBlocked(client)) return false;

        Optional<ClientData> dataByClientId = clientDataDao.findByClientId(client.getId());
        if (dataByClientId.isPresent()) {
            clientData = dataByClientId.get();
            clientDataDao.delete(clientData);
        }

        Optional<ClientDataType> optionalClientDataType = clientDataTypeDao.findByDescription("blocked");
        if (!optionalClientDataType.isPresent()) return false;
        ClientDataType blocked = optionalClientDataType.get();

        clientData = new ClientData();
        clientData.setPostStamp(now());
        clientData.setClientId(client.getId());
        clientData.setTypeId(blocked.getId());
        clientData.setValue("block");

        ClientData data = clientDataDao.save(clientData);

        return clientData.equals(data);
    }

    public Client getClientByLoginBlocked(String login) {
        Client clientByLogin = getClientByLogin(login);
        if (clientByLogin == null) return null;
        if (!clientIsBlocked(clientByLogin)) return null;
        return clientByLogin;
    }

    public Client getClientByLoginActivated(String login) {
        Client clientByLogin = getClientByLogin(login);
        if (clientByLogin == null) return null;
        if (!clientIsActivated(clientByLogin)) return null;
        return clientByLogin;
    }
    public Client getClientByLoginActivatedOrBlocked(String login) {
        Client activated = getClientByLoginActivated(login);
        if (activated == null)
            return getClientByLoginBlocked(login);
        return activated;
    }
    public Client getClientByLoginCreated(String login) {
        Client clientByLogin = getClientByLogin(login);
        if (clientByLogin == null) return null;
        if (!clientIsCreated(clientByLogin)) return null;
        return clientByLogin;
    }

    public Client getClientByLogin(String login) {
        if (login == null || login.isEmpty()) return null;

        Optional<Client> clientByLogin = clientDao.findByLogin(login);
        return clientByLogin.orElse(null);

    }

    public Client getClientById(Long id) {
        if (id == null) return null;
        Optional<Client> clientByLogin = clientDao.findById(id);
        return clientByLogin.orElse(null);
    }

    private boolean clientIsCreated(Client client) {
        Optional<ClientDataType> optionalClientDataType = clientDataTypeDao.findByDescription("created");
        if (!optionalClientDataType.isPresent())
            throw new RuntimeException("client data type \"created\" not found");
        ClientDataType blocked = optionalClientDataType.get();
        ClientData clientData = clientDataDao.findByTypeIdAndClientId(blocked.getId(), client.getId());
        return clientData != null;
    }

    public boolean clientIsActivated(Client client) {
        Optional<ClientDataType> optionalClientDataType = clientDataTypeDao.findByDescription("confirmed");
        if (!optionalClientDataType.isPresent())
            throw new RuntimeException("client data type \"confirmed\" not found");
        ClientDataType blocked = optionalClientDataType.get();
        ClientData clientData = clientDataDao.findByTypeIdAndClientId(blocked.getId(), client.getId());
        return clientData != null;
    }

    public boolean clientIsBlocked(Client client) {
        Optional<ClientDataType> optionalClientDataType = clientDataTypeDao.findByDescription("blocked");
        if (!optionalClientDataType.isPresent())
            throw new RuntimeException("client data type \"blocked\" not found");
        ClientDataType blocked = optionalClientDataType.get();
        ClientData clientData = clientDataDao.findByTypeIdAndClientId(blocked.getId(), client.getId());
        return clientData != null;
    }

    public boolean activateClient(Client client, String code) {
        Optional<ClientDataType> activated = clientDataTypeDao.findByDescription("confirmed");
        if (!activated.isPresent())
            return false;//todo ClientDataType table have not selected type

        Optional<ClientData> clientDataByClientId = clientDataDao.findByClientId(client.getId());
        if (clientDataByClientId.isPresent()) {
            if (!checkClientDataCode(client.getId(), code))
                return false;//todo when code invalid
            clientDataDao.delete(clientDataByClientId.get());
        }

        ClientData data = ClientData.builder()
            .value(client.getLogin())
            .postStamp(now())
            .clientId(client.getId())
            .typeId(activated.get().getId())
            .build();

        ClientData save = clientDataDao.save(data);

        client.setEnabled(data.getTypeId().equals(save.getTypeId()));
        log.info(client.toString());
        return client.isEnabled();
    }


    public boolean checkClientDataCode(Long clientId, String code) {
        Optional<ClientData> clientDataByClientId = clientDataDao.findByClientId(clientId);
        if (!clientDataByClientId.isPresent()) return false;
        ClientData clientData = clientDataByClientId.get();

        Optional<ClientDataType> dataTypeById = clientDataTypeDao.findById(clientData.getTypeId());
        if (!dataTypeById.isPresent())
            throw new RuntimeException(String.format("client data type \"%s\" not found", clientData.getTypeId().toString()));
        ClientDataType dataType = dataTypeById.get();

        if (dataType.getDescription().equals("created") || dataType.getDescription().equals("change email"))
            return clientData.getValue().equals(code);

        return false;
    }

    public ClientData getClientDataByClientLogin(String clientLogin) {
        Optional<Client> optionalClient = clientDao.findByLogin(clientLogin);
        if(!optionalClient.isPresent())
            return null;
        Optional<ClientData> dataByClientId = clientDataDao.findByClientId(optionalClient.get().getId());
        return dataByClientId.get();
    }
}
