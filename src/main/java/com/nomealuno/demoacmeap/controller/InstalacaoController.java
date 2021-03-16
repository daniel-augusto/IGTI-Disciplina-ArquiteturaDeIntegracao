package com.nomealuno.demoacmeap.controller;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.nomealuno.demoacmeap.domain.Cliente;
import com.nomealuno.demoacmeap.domain.Instalacao;
import com.nomealuno.demoacmeap.exception.RecursoNotFoundException;
import com.nomealuno.demoacmeap.repository.ClienteRepository;
import com.nomealuno.demoacmeap.repository.InstalacaoRepository;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@Api(value = "Acme AP Instalação Service")
public class InstalacaoController {

	@Autowired
	private InstalacaoRepository instalacaoRepository;

	@Autowired
	private ClienteRepository clienteRepository;

	@ApiOperation(value = "Mostra a lista de instalações")
	// Controle de versão explicito na URI
	@GetMapping("v1/instalacoes")
	public List<Instalacao> getAllInstalacoes() {

		try {
			ArrayList<Instalacao> listaInstalacoes = (ArrayList<Instalacao>) instalacaoRepository.findAll();

			return listaInstalacoes;

		} catch (Exception e) {
			throw new RecursoNotFoundException("Erro ao recuperar lista de instalações");
		}

	}

	@ApiOperation(value = "Consulta uma instalação pelo código")
	@GetMapping("v1/instalacoes/{codigo}")
	public Optional<Instalacao> getInstalacao(@PathVariable String codigo) {

		try {
			Optional<Instalacao> instalacao = instalacaoRepository.findByCodigo(codigo);

			if (instalacao.isEmpty()) {
				throw new RecursoNotFoundException("codigo instalacao - " + codigo);
			}

			return instalacao;
		} catch (Exception e) {
			throw new RecursoNotFoundException("codigo instalacao - " + codigo);
		}

	}

	@ApiOperation(value = "Consulta uma instalação pelo CPF")
	@GetMapping("v1/instalacoes/cpf/{cpf}")
	public List<Instalacao> getInstalacaoPorCPF(@PathVariable String cpf) {

		try {
			List<Instalacao> listaInstalacao = null;
			Optional<Cliente> cliente = clienteRepository.findByCpf(cpf);

			if (cliente.isEmpty()) {
				throw new RecursoNotFoundException("CPF - " + cpf);
			}

			listaInstalacao = instalacaoRepository.findByCliente(cliente.get());

			return listaInstalacao;
		} catch (Exception e) {
			throw new RecursoNotFoundException("CPF inválido - " + cpf);
		}

	}

	@ApiOperation(value = "Cadastrar uma nova instalação")
	@PostMapping("v1/instalacoes")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Object> cadastrarInstalacao(@RequestBody Instalacao instalacao) {
		try {

			Optional<Cliente> cliente = clienteRepository.findByCpf(instalacao.getCliente().getCpf());

			if (cliente.isEmpty()) {
				throw new RecursoNotFoundException("CPF - " + instalacao.getCliente().getCpf());
			}

			instalacao.setCliente(cliente.get());

			Instalacao instalacaoCriada = instalacaoRepository.save(instalacao);

			URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
					.buildAndExpand(instalacaoCriada.getId()).toUri();

			return ResponseEntity.created(location).build();

		} catch (Exception e) {
			throw new RecursoNotFoundException("CPF - " + instalacao.getCliente().getCpf());
		}

	}

}
