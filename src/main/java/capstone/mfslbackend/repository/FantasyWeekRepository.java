package capstone.mfslbackend.repository;
import capstone.mfslbackend.model.FantasyWeek;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


public interface FantasyWeekRepository extends JpaRepository<FantasyWeek, Long>, JpaSpecificationExecutor<FantasyWeek> {
}
