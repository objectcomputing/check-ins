package com.objectcomputing.checkins.services.skills;

import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.util.UUID;

@Controller("/skill")
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

    protected URI location(UUID uuid) {
        return URI.create("/skill/" + uuid);
    }

}
