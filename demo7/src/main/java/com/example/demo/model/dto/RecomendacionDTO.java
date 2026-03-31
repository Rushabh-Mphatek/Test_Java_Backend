package com.example.demo.model.dto;

import java.io.Serializable;

import com.example.demo.repository.entity.Cliente;
import com.example.demo.repository.entity.Recomendacion;
import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.Data;
import lombok.ToString;

@Data
public class RecomendacionDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	private Long id;
	private String observaciones;
	@ToString.Exclude
	@JsonBackReference
	private ClienteDTO clienteDTO;

	// Convertir de Entidad a DTO
	public static RecomendacionDTO convertToDTO(Recomendacion recomendacion, ClienteDTO clienteDTO) {

		RecomendacionDTO recomendacionDTO = new RecomendacionDTO();
		if (recomendacion != null) {
			recomendacionDTO.setId(recomendacion.getId());
			recomendacionDTO.setObservaciones(recomendacion.getObservaciones());
		} else {
			recomendacionDTO.setId(null);
			recomendacionDTO.setObservaciones(null);
		}

		recomendacionDTO.setClienteDTO(clienteDTO);
		return recomendacionDTO;
	}

	// Convertir de DTO a Entidad
	public static Recomendacion convertToEntity(RecomendacionDTO recomendacionDTO, Cliente cliente) {

		Recomendacion recomendacion = new Recomendacion();
		recomendacion.setId(recomendacionDTO.getId());
		recomendacion.setObservaciones(recomendacionDTO.getObservaciones());
		recomendacion.setCliente(cliente);

		return recomendacion;
	}
}
