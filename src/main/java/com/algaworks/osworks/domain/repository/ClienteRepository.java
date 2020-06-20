package com.algaworks.osworks.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.algaworks.osworks.domain.model.Cliente;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
	//não precisa declarar métodos, o JPA já tem o necessário criado
	//mas se necessário é possível criar Query Methods:
	
	//Retorna o nome exatamente igual ao passado por parâmetro
	List<Cliente> findByNome(String nome);
	
	//Retorna os nomes que conter a string passada por parâmetro
	List<Cliente> findByNomeContaining(String nome);
	
	//Retorna um cliente por email ou null se o email nao existir
	Cliente findByEmail(String email);
}
