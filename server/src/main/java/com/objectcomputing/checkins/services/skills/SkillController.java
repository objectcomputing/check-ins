package com.objectcomputing.checkins.services.skills;

import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.validation.Valid;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Controller("/skill")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name="skill")
public class SkillController {

    private static final Logger LOG = LoggerFactory.getLogger(SkillController.class);

    @Inject
    private SkillRepository skillRepo;

    @Inject
    private SkillServices skillsService;

    public void setSkillRepo(SkillRepository skillsRepository) {
        this.skillRepo = skillsRepository;
    }

    /**
     * Create and save a new skill.
     *
     * @param skill
     * @return
     */

    @Post(value = "/")
    public HttpResponse<Skill> createASkill(@Body @Valid Skill skill) {
        Skill newSkill = skillsService.saveSkill(skill);

        LOG.debug("newSkill = " + newSkill);

        if (newSkill == null) {
            return HttpResponse.status(HttpStatus.valueOf(409), "already exists");
        } else {
            return HttpResponse
                    .created(newSkill)
                    .headers(headers -> headers.location(location(newSkill.getSkillid())));
        }
    }
//
//    @Post(value = "/createtest")
//    @Consumes(MediaType.APPLICATION_JSON)
//    public void createATestSkill(@Body Skill skill) {
//        LOG.info("skills stored.");
//        Skill returned = skillsService.saveSkill(skill);
//        LOG.info("returned = " + returned);
//    }

    @Post("/loadskills")
    @Consumes(MediaType.APPLICATION_JSON)
    public void loadSkills(@Body Skill[] skillslist) {

        skillsService.loadSkills(skillslist);

    }

    /**
     * Find and read a skill or skills given its id, name, or pending status.
     *
     * @param skillid
     * @param name
     * @param pending
     * @return
     */

    @Get("/{?skillid,name,pending}")
    public List<Skill> findByValue(@Nullable UUID skillid, @Nullable String name, @Nullable Boolean pending) {

        List<Skill> found = skillsService.findByValue(skillid, name, pending);
        return found;

    }

    /**
     * Update the pending status of a skill.
     * @param skill
     * @return
     */
    @Put("/updatePending")
    public HttpResponse<?> updatePending(@Body @Valid Skill skill) {

        if(null != skill.getSkillid()) {
            Skill updatedSkill = skillsService.updatePending(skill);
            return HttpResponse
                    .ok()
                    .headers(headers -> headers.location(location(updatedSkill.getSkillid())))
                    .body(updatedSkill);
        }

        return HttpResponse.badRequest();
    }

    protected URI location(UUID uuid) {
        return URI.create("/skill/" + uuid);
    }

}