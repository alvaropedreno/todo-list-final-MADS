package madstodolist.repository;

import madstodolist.model.Comentario;
import org.springframework.data.repository.CrudRepository;

public interface ComentarioRepository extends CrudRepository<Comentario, Long> {
}
