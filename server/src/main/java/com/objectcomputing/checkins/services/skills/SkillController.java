package com.objectcomputing.checkins.services.skills;

import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.swagger.v3.oas.annotations.Parameter;
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

//    @Post(value = "create")
//    @Consumes(MediaType.APPLICATION_JSON)
//    public void createSkills(@Body Skill skill) {
//        LOG.info("skills stored.");
//        List<Skill> skillsList = skillsService.saveSkill(skill);
//    }

    /**
     * Create and save a new skill.
     *
     * @param skill
     * @return
     */

    @Post(value = "create")
    public HttpResponse<Skill> createASkill(@Body @Valid Skill skill) {
        LOG.info("skills stored.");
        Skill newSkill = skillsService.saveSkill(skill);
        LOG.info("newSkill = " + newSkill);
        return HttpResponse
                .created(newSkill)
                .headers(headers -> headers.location(location(newSkill.getSkillid())));
    }

    @Post(value = "createtest")
    @Consumes(MediaType.APPLICATION_JSON)
    public void createATestSkill(@Body Skill skill) {
        LOG.info("skills stored.");
        Skill returned = skillsService.saveSkill(skill);
        LOG.info("returned = " + returned);
    }

    @Get("testloadskills")
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
     * Find and read a skill given its id .
     *
     * @param skillid
     * @return
     */
//  /skill/{id}
    @Get("/{skillid}")
    public Skill readSkill(@PathVariable UUID skillid) {
        LOG.info("read skill by id: " + skillid);
        Skill foundSkill = skillsService.readSkill(skillid);
        LOG.info("found skill by id: " + foundSkill + " " + foundSkill.getSkillid());
        return foundSkill;
    }

    protected URI location(UUID uuid) {
        return URI.create("/skill/" + uuid);
    }

    /**
     * Find Skill by Name.
     *
     * @param name
     * @return
     */

    // /skill/?name=blah
    @Get("/{?name,pending}")
    public List<Skill> findByName(@Nullable String name,@Nullable boolean pending) {

        List<Skill> found = skillsService.findByName(name);
        return skillsService.findByName(name);

    }

}