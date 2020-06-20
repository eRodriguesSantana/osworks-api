package com.algaworks.osworks.api.controller;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.algaworks.osworks.domain.model.Cliente;
import com.algaworks.osworks.domain.repository.ClienteRepository;
import com.algaworks.osworks.domain.service.CadastroClienteService;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

	@Autowired
	private ClienteRepository clienteRepository;
	
	@Autowired
	private CadastroClienteService cadastroCliente;
	
	@GetMapping
	public List<Cliente> listar() {		
		return clienteRepository.findAll();
		
		//Exemplos de uso de métodos personalizados criados na classe ClienteRepository
		//return clienteRepository.findByNome("João da Silva");
		//return clienteRepository.findByNomeContaining("Si");
	}
	
	@GetMapping("/{clienteId}")
	//metódo que retorna um ResponseEntity permite alterar o status HTTP da requisição (200 OK, 201 CREATED, etc...) 
	public ResponseEntity<Cliente> buscar(@PathVariable Long clienteId) {
		Optional<Cliente> cliente = clienteRepository.findById(clienteId);
		
		if(cliente.isPresent()) {
			//se o código de consulta passado por parâmetro for encontrado no banco de dados retorna status HTTP 200 (ok)
			//ResponseEntity.ok(cliente.get()) - retorna no corpo da requisição o objeto encontrado
			return ResponseEntity.ok(cliente.get());
		}
		
		//Se o código passado não existir no banco de dados, retorna status HTTP 404 sem corpo nenhum no retorno da requisição
		return ResponseEntity.notFound().build();
	}
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED) //retorna o status 201 se a criação for realizada com sucesso
	//A annotation @RequestBody permite receber o corpo da requisição no parâmetro Java passado no método
	//A annotation @Valid ativa as annotations de validação inseridas na classe Cliente (Jakarta Bean Validation)
	public Cliente adicionar(@Valid @RequestBody Cliente cliente) {
		return cadastroCliente.salvar(cliente);
	}
	
	@PutMapping("/{clienteId}")
	//A annotation @PathVariable permite fazer um bind do parâmetro Long clienteId com a consulta JPA
	//A annotation @RequestBody permite receber o corpo da requisição no parâmetro Java passado no método
	//A annotation @Valid ativa as annotations de validação inseridas na classe Cliente (Jakarta Bean Validation)
	public ResponseEntity<Cliente> atualizar(@Valid @PathVariable Long clienteId, 
											 @RequestBody Cliente cliente) {
		
		//se o ID não existir no banco de dados, retorna HTTP status 404
		if(!clienteRepository.existsById(clienteId)) {
			return ResponseEntity.notFound().build();
		}
		
		//O valor do parâmetro clienteId é usado para que o JPA altere exatamente o registro encontrado no banco de dados
		//de mesmo id
		cliente.setId(clienteId);
		cliente = cadastroCliente.salvar(cliente);
		
		//Como se trata de atualização, o retorno HTTP pode ser 200
		return ResponseEntity.ok(cliente);
		
	}
	
	@DeleteMapping("/{clienteId}")
	public ResponseEntity<Void> remover(@PathVariable Long clienteId){
		
		//se o ID não existir no banco de dados, retorna HTTP status 404
		if(!clienteRepository.existsById(clienteId)) {
			return ResponseEntity.notFound().build();
		}
		
		cadastroCliente.excluir(clienteId);
		
		//Retorna HTTP status 204 (sem conteúdo)
		return ResponseEntity.noContent().build();
	}
}
