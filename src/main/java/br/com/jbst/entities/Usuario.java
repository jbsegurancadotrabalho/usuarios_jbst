package br.com.jbst.entities;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;

@Entity
@Table(name = "usuario")
@Data
public class Usuario {

	@Id 	// Campo 1
	@Column(name = "id")
	private UUID id;

	// Campo 2
	@Column(name = "nome", length = 100, nullable = false)
	private String nome;

	// Campo 3
	@Column(name = "email", length = 50, nullable = false, unique = true)
	private String email;

	// Campo 4
	@Column(name = "senha", length = 40, nullable = false)
	private String senha;

	// Campo 5
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "datahoracriacao", nullable = false)
	private Instant dataHoraCriacao;

	// Campo 6
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "datahoraalteracao", nullable = false)
	private Instant dataHoraAlteracao;

	// Campo 7
	@ManyToOne // muitos usuários para 1 perfil
	@JoinColumn(name = "perfil_id", nullable = false) // O JoinColumn é para mapeamento de chave estrangeira//
	private Perfil perfil;
}
