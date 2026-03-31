package com.example.demo.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.dto.ClienteDTO;
import com.example.demo.repository.dao.ClienteRepository;
import com.example.demo.repository.entity.Cliente;

@Service
public class ClienteServiceImpl implements ClienteService {

	private static final Logger log = LoggerFactory.getLogger(ClienteServiceImpl.class);

	@Autowired
	private ClienteRepository clienteRepository;

	@Override
	public List<ClienteDTO> findAll() {

		log.info("ClienteServiceImpl - findAll(): Lista de todos los clientes");

		List<ClienteDTO> listaClientesDTO = new ArrayList<ClienteDTO>();
		List<Cliente> listaClientes = clienteRepository.findAll();

		for (int i = 0; i < listaClientes.size(); i++) {
			Cliente cliente = listaClientes.get(i);
			ClienteDTO clienteDTO = ClienteDTO.convertToDTO(cliente);
			listaClientesDTO.add(clienteDTO);
		}

//		List<ClienteDTO> listaClientesDTO = clienteRepository.findAll().stream().map(p -> ClienteDTO.convertToDTO(p))
//				.collect(Collectors.toList());

		return listaClientesDTO;
	}

	@Override
	public ClienteDTO findById(ClienteDTO clienteDTO) {
		log.info("ClienteServiceImpl - findById(): Busca un cliente por ID" + clienteDTO.getId());

		Optional<Cliente> cliente = clienteRepository.findById(clienteDTO.getId());

		if (cliente.isPresent()) {
			clienteDTO = ClienteDTO.convertToDTO(cliente.get());
			return clienteDTO;
		} else {
			return null;
		}
	}

	@Override
	public ClienteDTO save(ClienteDTO clienteDTO) {

		log.info("ClienteServiceImpl - save(): Guardamos el cliente: " + clienteDTO.toString());

		Cliente cliente = ClienteDTO.convertToEntity(clienteDTO);
		cliente = clienteRepository.save(cliente);

		return clienteDTO.convertToDTO(cliente);
	}

	@Override
	public void delete(ClienteDTO clienteDTO) {
		log.info("ClienteServiceImpl - delete(): Borramos el cliente: " + clienteDTO.getId());

		Cliente cliente = new Cliente();
		cliente.setId(clienteDTO.getId());
		clienteRepository.delete(cliente);
	}

	@Override
	public List<ClienteDTO> findByApellidos(String apellidos) {
		// TODO Auto-generated method stub
		return null;
	}

}
