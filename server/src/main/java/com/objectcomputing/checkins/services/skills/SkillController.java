package com.objectcomputing.checkins.services.skills;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;

@Controller("/skill")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name="skill")
public class SkillController {

    private static final Logger LOG = LoggerFactory.getLogger(SkillController.class);

//    @Inject
//    private SkillRepository skillRepo;

    @Inject
    private SkillServices skillsService;

//    public void setSkillRepo(SkillRepository skillsRepository) {
//        this.skillRepo = skillsRepository;
//    }

    /**
     * Create and save a new skill.
     *
     * @param skill
     * @return
     */

    @Post(value = "/create")
    public HttpResponse<Skill> createASkill(@Body @Valid Skill skill) {
        LOG.info("skills stored.");
        Skill newSkill = skillsService.saveSkill(skill);
        LOG.info("newSkill = " + newSkill);
        return HttpResponse
                .created(newSkill)
                .headers(headers -> headers.location(location(newSkill.getSkillid())));
    }

    @Post(value = "/createtest")
    @Consumes(MediaType.APPLICATION_JSON)
    public void createATestSkill(@Body Skill skill) {
        LOG.info("skills stored.");
        Skill returned = skillsService.saveSkill(skill);
        LOG.info("returned = " + returned);
    }

    @Get("/testloadskills")
    public void testCreateSkills() {

        LOG.info("testing skills stored.");
        Skill newSkill = new Skill("java", true);
        Skill returned = skillsService.saveSkill(newSkill);
        LOG.info("returned = " + returned);

        newSkill = new Skill("tensorflow", false);
        returned = skillsService.saveSkill(newSkill);
        LOG.info("returned = " + returned);

        newSkill = new Skill("Docker");
        returned = skillsService.saveSkill(newSkill);
        LOG.info("returned = " + returned.toString());

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
        LOG.info("SkillController");
        LOG.info("finding skills by values." +skillid + " " + name + " " + pending);

        List<Skill> found = skillsService.findByValue(skillid, name, pending);
        return found;

    }

    protected URI location(UUID uuid) {
        return URI.create("/skill/" + uuid);
    }

}