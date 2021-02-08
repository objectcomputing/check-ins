import React, { useContext, useEffect, useState } from "react";

import "./SkillSection.css"

import {
  AppContext,
  ADD_SKILL,
  ADD_MEMBER_SKILL,
  DELETE_MEMBER_SKILL,
  UPDATE_MEMBER_SKILLS,
  selectMySkills,
  selectCurrentUser,
  selectCurrentUserId
} from "../../context/AppContext";
import { createMemberSkill, deleteMemberSkill, updateMemberSkill } from "../../api/memberskill.js";
import { getSkill, createSkill } from "../../api/skill.js";
import Search from "../profile/Search";
import SkillSlider from "./SkillSlider"

import { Card, CardActions, CardHeader } from "@material-ui/core";
import Box from "@material-ui/core/Box";

const SkillSection = ({userId}) => {
  const { state, dispatch } = useContext(AppContext);
  const { csrf, skills } = state;
  const myMemberSkills = selectMySkills(state);
  const [mySkills, setMySkills] = useState([]);

  const mapMemberSkill = async (memberSkill, csrf) => {
    let thisSkill = await getSkill(memberSkill.skillid, csrf);
    thisSkill.lastuseddate = memberSkill.lastuseddate;
    thisSkill.skilllevel = memberSkill.skilllevel;
    return thisSkill;
  }

  useEffect(() => {
    const getSkills = async () => {
      const skillsResults = await Promise.all(
        myMemberSkills.map((mSkill) => mapMemberSkill(mSkill, csrf))
      );
      console.log(skillsResults);
      const currentUserSkills = skillsResults.map(
        (result) => {
          let skill = result.payload.data;
          skill.skilllevel = result.skilllevel;
          skill.lastuseddate = result.lastuseddate;
          return skill;
        }
      );
      setMySkills(currentUserSkills);
    };
    if (csrf && myMemberSkills) {
      getSkills();
    }
  }, [csrf, myMemberSkills]);

  const addSkill = async (name) => {
    if (!csrf) {
      return;
    }
    const inSkillsList = skills.find(
      (skill) => skill.name.toUpperCase() === name.toUpperCase()
    );
    let curSkill = inSkillsList;
    if (!inSkillsList) {
      const res = await createSkill({ name: name, pending: true }, csrf);
      const data =
        res && res.payload && res.payload.data ? res.payload.data : null;
      data && dispatch({ type: ADD_SKILL, payload: data });
      curSkill = data;
    }
    if (curSkill && curSkill.id && userId) {
      if (
        Object.values(mySkills).find(
          (skill) => skill.name.toUpperCase === curSkill.name.toUpperCase()
        )
      ) {
        return;
      }
      const res = await createMemberSkill(
        { skillid: curSkill.id, memberid: userId },
        csrf
      );
      const data =
        res && res.payload && res.payload.data ? res.payload.data : null;
      data && dispatch({ type: ADD_MEMBER_SKILL, payload: data });
    }
  };

  const removeSkill = async (id, csrf) => {
    const mSkill = myMemberSkills.find((s) => s.skillid === id);
    await deleteMemberSkill(mSkill.id, csrf);
    dispatch({ type: DELETE_MEMBER_SKILL, payload: id });
  };

  const handleDelete = (id) => {
    if (csrf && id) {
      removeSkill(id, csrf);
    }
  };

  const handleUpdate = async (lastUsedDate, skillLevel, index) => {
    console.log(csrf);
    console.log(lastUsedDate);
    console.log(skillLevel);
    if (csrf && skillLevel) {
        let copy = [...myMemberSkills];
        copy[index].lastuseddate = lastUsedDate;
        copy[index].skilllevel = skillLevel;
        let postUpdate = await updateMemberSkill(copy[index], csrf);
        dispatch({ type: UPDATE_MEMBER_SKILLS, payload: copy });
    }
  };

  return (
      <Card width="100%">
        <h2>Skills</h2>
        <Search mySkills={Object.values(mySkills)} addSkill={addSkill} />
        {mySkills &&
          mySkills.map((memberSkill, index) => {
            return (
              <SkillSlider
                key={memberSkill.id}
                id={memberSkill.id}
                name={memberSkill.name}
                startLevel={memberSkill.skilllevel ? memberSkill.skilllevel : 3}
                lastUsedDate={memberSkill.lastuseddate}
                onDelete={handleDelete}
                onUpdate={handleUpdate}
                index={index}
              />
            );
          })}
      </Card>
  );

};
export default SkillSection;