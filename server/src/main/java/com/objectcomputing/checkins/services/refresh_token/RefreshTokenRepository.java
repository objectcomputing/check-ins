package com.objectcomputing.checkins.services.refresh_token;



import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface RefreshTokenRepository extends CrudRepository<RefreshToken, UUID> {


  @Query(value = "SELECT id, refresh_token, user_name, revoked, date_created " +
          "FROM \"refresh_token\" rt " +
          "WHERE rt.refresh_token = :refreshToken", nativeQuery = true)
  Optional<RefreshToken> findByRefreshToken(String refreshToken);

}
