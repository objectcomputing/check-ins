package com.objectcomputing;

import java.util.Optional;
import com.objectcomputing.member.MemberProfile;

public interface MemberProfileRepository {
 
 MemberProfile createProfile(String name);

 Optional<MemberProfile> findBy(String name,String role,String pdlId) ;
 
 String update(String name,String pdlId);

 MemberProfile getProfile(String id);

}