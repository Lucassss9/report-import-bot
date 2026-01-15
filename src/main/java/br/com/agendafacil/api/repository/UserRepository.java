package br.com.agendafacil.api.repository;

import br.com.agendafacil.api.domain.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<Usuario, Long> {
}
