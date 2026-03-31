package com.example.demo.repository.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.repository.entity.ClienteDireccion;

import jakarta.transaction.Transactional;

@Transactional
@Repository
public interface ClienteDireccionRepository extends JpaRepository<ClienteDireccion, Long> {
	@Query(value = "SELECT c FROM ClienteDireccion c WHERE c.cliente.id = :idcli AND c.direccion.id = :iddir")
	public Optional<ClienteDireccion> findAllByClienteDireccion(@Param("idcli") Long idCliente,
			@Param("iddir") Long idDireccion);

	@Query(value = "SELECT c FROM ClienteDireccion c WHERE c.cliente.id = :idcli")
	public List<ClienteDireccion> findAllByCliente(@Param("idcli") Long idCliente);
}