package com.dev.taskmanager.repositories;

import com.dev.taskmanager.entities.User;
import com.dev.taskmanager.projections.UserDetailsProjection;
import com.dev.taskmanager.projections.UserProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query(nativeQuery = true, value = """
			SELECT tb_user.email AS username, tb_user.password, tb_role.id AS roleId, tb_role.authority
			FROM tb_user
			INNER JOIN tb_user_role ON tb_user.id = tb_user_role.user_id
			INNER JOIN tb_role ON tb_role.id = tb_user_role.role_id
			WHERE tb_user.email = :email
		""")
    List<UserDetailsProjection> searchUserAndRolesByEmail(String email);

    @Query(nativeQuery = true, value = """
			SELECT tb_user.*, tb_role.id AS roleId, tb_role.authority
			 FROM tb_user
			 INNER JOIN tb_user_role ON tb_user.id=tb_user_role.user_id
			 INNER JOIN tb_role ON tb_user_role.role_id=tb_role.id
		""")
    List<UserProjection> searchUserAndRoles();

	Optional<User> findByEmail(String email);
}
