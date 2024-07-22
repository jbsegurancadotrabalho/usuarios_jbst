package br.com.jbst.repositories;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import br.com.jbst.entities.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {


	@Query( "select u from Usuario u " +
			"join u.perfil p " + 
			"where u.email = :email")
	public Usuario findByEmail(
			@Param("email") String email);
	
	@Query(	"select u from Usuario u " +
			"join u.perfil p " +
			"where u.email = :email " + 
			"  and u.senha = :senha")
	public Usuario findByEmailAndSenha(
			@Param("email") String email,
			@Param("senha") String senha);
}
