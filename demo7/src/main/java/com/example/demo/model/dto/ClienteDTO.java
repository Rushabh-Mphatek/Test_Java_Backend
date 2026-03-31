package com.example.demo.model.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import com.example.demo.repository.entity.Cliente;
import com.example.demo.repository.entity.ClienteDireccion;
import com.example.demo.repository.entity.Cuenta;
import com.example.demo.repository.entity.Recomendacion;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;
import lombok.ToString;

@Data
public class ClienteDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	private Long id;
	private String nif;
	private String nombre;
	private String apellidos;
	private String claveSeguridad;
	private String email;
	@ToString.Exclude
	@JsonManagedReference
	private RecomendacionDTO recomendacionDTO;
	@ToString.Exclude
	@JsonManagedReference
	private List<CuentaDTO> listaCuentasDTO;
//@ToString.Exclude
	// private List<DireccionDTO> listaDireccionesDTO;

	@ToString.Exclude
	@JsonIgnore
	private List<ClienteDireccionDTO> listaClientesDireccionesDTO;

	@Temporal(TemporalType.DATE)
	@DateTimeFormat(iso = ISO.DATE)
	private Date fechaNacimiento;

	// Convierte una entidad a DTO (texto plano para su posterior lectura en el lado
	// del cliente)
	public static ClienteDTO convertToDTO(Cliente cliente) {

		ClienteDTO clienteDTO = new ClienteDTO();
		clienteDTO.setId(cliente.getId());
		clienteDTO.setNif(cliente.getNif());
		clienteDTO.setNombre(cliente.getNombre());
		clienteDTO.setApellidos(cliente.getApellidos());
		clienteDTO.setClaveSeguridad(cliente.getClaveSeg());
		clienteDTO.setEmail(cliente.getEmail());
		clienteDTO.setFechaNacimiento(cliente.getFechaNacimiento());

		RecomendacionDTO rec = RecomendacionDTO.convertToDTO(cliente.getRecomendacion(), clienteDTO);
		clienteDTO.setRecomendacionDTO(rec);

		// Cargamos la lista de cuentas, que como es un HashSet hemos de convertirla a
		// ArrayList
		List<Cuenta> listaCuentas = new ArrayList<Cuenta>(cliente.getListaCuentas());
		// Cargamos la lista de cuentas
		for (int i = 0; i < listaCuentas.size(); i++) {
			CuentaDTO cuentaDTO = CuentaDTO.convertToDTO(listaCuentas.get(i), clienteDTO);
			clienteDTO.getListaCuentasDTO().add(cuentaDTO);
		}

//		List<Direccion> listaDirecciones = new ArrayList<Direccion>(cliente.getListaDirecciones());
//		for (int i = 0; i < listaDirecciones.size(); i++) {
//			DireccionDTO direccionDTO = DireccionDTO.convertToDTO(listaDirecciones.get(i), clienteDTO);
//			clienteDTO.getListaDireccionesDTO().add(direccionDTO);
//		}

//		for (int i = 0; i < listaClientesDirecciones.size(); i++) {
//			// Como solo nos interesa la direccion la lista que tenemos sera lista de
//			// direccionesDTO
//			DireccionDTO direccionDTO = DireccionDTO.convertToDTO(listaClientesDirecciones.get(i).getDireccion(),
//					clienteDTO);
//			clienteDTO.getListaDireccionesDTO().add(direccionDTO);
//		}

		// Cargamos la lista de clientes direcciones, que como es un HashSet hemos de
		// convertirla a ArrayList
		List<ClienteDireccion> listaClientesDirecciones = new ArrayList<ClienteDireccion>(
				cliente.getListaClientesDirecciones());

		for (int i = 0; i < listaClientesDirecciones.size(); i++) {
			ClienteDireccionDTO clienteDireccionDTO = new ClienteDireccionDTO();
			clienteDireccionDTO.setClienteDTO(clienteDTO);

			clienteDireccionDTO.setDireccionDTO(
					DireccionDTO.convertToDTO(listaClientesDirecciones.get(i).getDireccion(), clienteDTO));

			clienteDireccionDTO.setFechaAlta(listaClientesDirecciones.get(i).getFechaAlta());
			clienteDTO.getListaClientesDireccionesDTO().add(clienteDireccionDTO);
		}

		return clienteDTO;
	}

	// Convierte un DTO a una entidad
	public static Cliente convertToEntity(ClienteDTO clienteDTO) {
		Cliente cliente = new Cliente();
		cliente.setId(clienteDTO.getId());
		cliente.setNif(clienteDTO.getNif());
		cliente.setNombre(clienteDTO.getNombre());
		cliente.setApellidos(clienteDTO.getApellidos());
		cliente.setClaveSeg(clienteDTO.getClaveSeguridad());
		cliente.setEmail(clienteDTO.getEmail());
		cliente.setFechaNacimiento(clienteDTO.getFechaNacimiento());

		Recomendacion rec = RecomendacionDTO.convertToEntity(clienteDTO.getRecomendacionDTO(), cliente);
		cliente.setRecomendacion(rec);

		// Cargamos la lista de cuentas
		for (int i = 0; i < clienteDTO.getListaCuentasDTO().size(); i++) {
			Cuenta cuenta = CuentaDTO.convertToEntity(clienteDTO.getListaCuentasDTO().get(i));
			cliente.getListaCuentas().add(cuenta);
		}

		// Cargamos la lista de direcciones
//		for (int i = 0; i < clienteDTO.getListaDireccionesDTO().size(); i++) {
//			Direccion direccion = DireccionDTO.convertToEntity(clienteDTO.getListaDireccionesDTO().get(i), cliente);
//			ClienteDireccion cd = new ClienteDireccion();
//			cd.setCliente(cliente);
//			cd.setDireccion(direccion);
//			cliente.getListaClientesDirecciones().add(cd);
//		}

		// Cargamos la lista de ClientesDireccionesDTO
		for (int i = 0; i < clienteDTO.getListaClientesDireccionesDTO().size(); i++) {
			ClienteDireccion cd = new ClienteDireccion();
			cd.setId(clienteDTO.getListaClientesDireccionesDTO().get(i).getId());
			cd.setCliente(cliente);

			cd.setDireccion(DireccionDTO
					.convertToEntity(clienteDTO.getListaClientesDireccionesDTO().get(i).getDireccionDTO(), cliente));

			cd.setFechaAlta(clienteDTO.getListaClientesDireccionesDTO().get(i).getFechaAlta());
			cliente.getListaClientesDirecciones().add(cd);
		}

		return cliente;
	}

	// Constructor vacio - inicializa el objeto recomendacion de la clase ClienteDTO
	public ClienteDTO() {
		super();
		this.recomendacionDTO = new RecomendacionDTO();
		this.listaCuentasDTO = new ArrayList<CuentaDTO>();
//		this.listaDireccionesDTO = new ArrayList<DireccionDTO>();
		this.listaClientesDireccionesDTO = new ArrayList<ClienteDireccionDTO>();
	}

}
