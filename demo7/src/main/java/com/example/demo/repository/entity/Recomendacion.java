package com.example.demo.repository.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.ToString;

@Data
@Entity
@Table(name = "recomendaciones")
public class Recomendacion {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String observaciones;
	@OneToOne(fetch = FetchType.EAGER, optional = false) // Indica la no obligatoriedad de la relacion, solo puede estar
															// en el extremo de la relacion, donde no tenemos el
															// mappedBy

	@JoinColumn(name = "idcliente") // Especifica la columna de clave externa en la base de datos
	/**
	 * El lado inverso de la relación contiene un atributo "mappedBy" para indicar
	 * que la relación está asignada por la otra entidad (Cliente)
	 */
	@ToString.Exclude
	private Cliente cliente;
}
