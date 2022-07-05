import React, { useContext, useEffect, useState } from "react";
import { styled } from '@mui/material/styles';
import "./SkillSection.css";
import { AppContext } from "../../context/AppContext";
import {
  selectMySkills,
  selectOrderedSkills,
  selectCsrfToken,
} from "../../context/selectors";
import {
  ADD_SKILL,
  ADD_MEMBER_SKILL,
  DELETE_MEMBER_SKILL,
  UPDATE_MEMBER_SKILLS,
  UPDATE_TOAST,
} from "../../context/actions";
import {
  createMemberSkill,
  deleteMemberSkill,
  updateMemberSkill,
} from "../../api/memberskill.js";
import { getSkill, createSkill } from "../../api/skill.js";
import SkillSlider from "./SkillSlider";

import {
  Button,
  Card,
  CardActions,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle,
  List,
  ListItem,
  Modal,
  TextField,
} from "@mui/material";
import Autocomplete, {
  createFilterOptions,
} from '@mui/material/Autocomplete';
import BuildIcon from "@mui/icons-material/Build";
import Typography from "@mui/material/Typography";

const PREFIX = 'SkillSection';
const classes = {
  skillRow: `${PREFIX}-skillRow`,
  components: `${PREFIX}-components`
};

const Root = styled('span')(() => ({
  [`& .${classes.skillRow}`]: {
    justifyContent: "center",
    fontWeight: "bold",
  }
}));

const SkillSection = ({ userId }) => {
  const { state, dispatch } = useContext(AppContext);
  const csrf = selectCsrfToken(state);
  const skills = selectOrderedSkills(state);
  const myMemberSkills = selectMySkills(state);
  const [mySkills, setMySkills] = useState([]);
  const [skillToAdd, setSkillToAdd] = useState({ name: "", description: "" });
  const [open, setOpen] = useState(false);
  const [openDelete, setOpenDelete] = useState(false);
  const [selectedSkillId, setSelectedSkillId] = useState(null);

  const handleOpen = () => setOpen(true);
  const handleClose = () => setOpen(false);

  const handleOpenDeleteConfirmation = () => setOpenDelete(true);
  const handleCloseDeleteConfirmation = () => setOpenDelete(false);

  const mapMemberSkill = async (memberSkill, csrf) => {
    let thisSkill = await getSkill(memberSkill.skillid, csrf);
    thisSkill.lastuseddate = memberSkill.lastuseddate;
    thisSkill.skilllevel = memberSkill.skilllevel;
    return thisSkill;
  };

  useEffect(() => {
    const getSkills = async () => {
      const skillsResults = await Promise.all(
        myMemberSkills.map((mSkill) => mapMemberSkill(mSkill, csrf))
      );
      const currentUserSkills = skillsResults.map((result) => {
        let skill = result.payload.data;
        skill.skilllevel = result.skilllevel;
        skill.lastuseddate = result.lastuseddate;
        return skill;
      });
      currentUserSkills.sort((a, b) => a.name.localeCompare(b.name));
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
      (skill) =>
        skill &&
        skill.name.toUpperCase() ===
          (name ? name.toUpperCase() : skillToAdd.name.toUpperCase())
    );
    let curSkill = inSkillsList;
    if (!inSkillsList) {
      handleOpen();
      const res = await createSkill(
        {
          ...skillToAdd,
          pending: true,
        },
        csrf
      );
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
        { skillid: curSkill.id, memberid: userId, skilllevel: 3},
        csrf
      );
      const data =
        res && res.payload && res.payload.data ? res.payload.data : null;
      data && dispatch({ type: ADD_MEMBER_SKILL, payload: data });
    }
    handleClose();
    setSkillToAdd({ name: "", description: "" });
  };

  const removeSkill = async (id, csrf) => {
    const mSkill = myMemberSkills.find((s) => s.skillid === id);
    const result = await deleteMemberSkill(mSkill.id, csrf);
    if (result && result.payload && result.payload.status === 200) {
      window.snackDispatch({
        type: UPDATE_TOAST,
        payload: {
          severity: "success",
          toast: "Skill removed from profile",
        },
      });
      dispatch({ type: DELETE_MEMBER_SKILL, payload: id });
      handleCloseDeleteConfirmation();
    }
  };

  const handleDelete = () => {
    if (csrf && selectedSkillId) {
      removeSkill(selectedSkillId, csrf);
    }
  };

  const handleUpdate = async (lastUsedDate, skillLevel, id) => {
    if (csrf && skillLevel) {
      const mSkill = {...myMemberSkills.find((s) => s.skillid === id)};
      mSkill.lastuseddate = lastUsedDate;
      mSkill.skilllevel = skillLevel;
      await updateMemberSkill(mSkill, csrf);
      let copy = [...myMemberSkills.filter((skill) => skill.id !== mSkill.id), mSkill];
      dispatch({ type: UPDATE_MEMBER_SKILLS, payload: copy });
    }
  };
  const filter = createFilterOptions();

  const SkillSelector = () => (
    <Autocomplete
      isOptionEqualToValue={(option, value) =>
        value ? value.id === option.id : false
      }
      value={skillToAdd}
      id="skillSearchAutocomplete"
      className="skill-search-autocomplete"
      selectOnFocus
      clearOnBlur={true}
      handleHomeEndKeys
      blurOnSelect
      options={skills
        .filter(
          (skill) => !mySkills.map((mSkill) => mSkill.id).includes(skill.id)
        )
        .map((skill) => {
          return {
            displayLabel: skill.name,
            name: skill.name,
          };
        })}
      renderOption={(props, option) => (
        <li {...props}>
          {option.displayLabel}
        </li>
      )}
      filterOptions={(options, params) => {
        const filtered = filter(options, params);

        if (params.inputValue !== "") {
          filtered.push({
            name: params.inputValue,
            displayLabel: `Add "${params.inputValue}"`,
          });
        }
        return filtered;
      }}
      renderInput={(params) => (
        <TextField
          {...params}
          className="fullWidth"
          label="Add a skill..."
          placeholder="Enter a skill name"
          variant="standard"
        />
      )}
      onChange={(event, value) => {
        if (value === null) return;
        const inSkillsList = skills.find(
          (skill) =>
            skill && skill.name.toUpperCase() === value.name.toUpperCase()
        );
        if (!inSkillsList) {
          setSkillToAdd({ name: value.name, description: "" });
          handleOpen();
        } else {
          addSkill(value.name);
        }
      }}
      getOptionLabel={(option) => option.displayLabel || ""}
    />
  );

  return (
    <>
      <Modal open={open} onClose={handleClose}>
          <div className="skill-modal">
            <TextField
              className="fullWidth"
              id="skill-name-input"
              label="Name"
              placeholder="Skill Name"
              required
              value={skillToAdd.name ? skillToAdd.name : ""}
              onChange={(e) =>
                setSkillToAdd({ ...skillToAdd, name: e.target.value })
              }
            />
            <TextField
              className="fullWidth"
              id="skill-description-input"
              label="Description"
              placeholder="Skill Description"
              required
              value={skillToAdd.description ? skillToAdd.description : ""}
              onChange={(e) =>
                setSkillToAdd({ ...skillToAdd, description: e.target.value })
              }
            />
            <div className="skill-modal-actions fullWidth">
              <Button onClick={handleClose} color="secondary">
                Cancel
              </Button>
              <Button onClick={() => addSkill(skillToAdd.name)} color="primary">
                Save Skill
              </Button>
            </div>
          </div>
        </Modal>
      <Root>
        <Card>
          <div className="skill-card-header">
            <div className="skill-card-header-title">
              <BuildIcon style={{ marginRight: "16px" }}/>
              <Typography variant="h5" component="h2">Skills</Typography>
            </div>
            <SkillSelector/>
          </div>
          <List>
            {mySkills &&
            mySkills.map((memberSkill) => {
              return (
                <ListItem
                  key={`MemberSkill-${memberSkill.id}`}
                  className={classes.skillRow}
                >
                  <SkillSlider
                    description={memberSkill.description}
                    id={memberSkill.id}
                    name={memberSkill.name}
                    startLevel={
                      memberSkill.skilllevel ? memberSkill.skilllevel : 3
                    }
                    lastUsedDate={memberSkill.lastuseddate}
                    onDelete={(id) => {
                      handleOpenDeleteConfirmation();
                      setSelectedSkillId(id);
                    }}
                    onUpdate={handleUpdate}
                  />
                </ListItem>
              );
            })}
          </List>
          <CardActions>
            <div>
              <Dialog
                open={openDelete}
                onClose={handleCloseDeleteConfirmation}
                aria-labelledby="alert-dialog-title"
                aria-describedby="alert-dialog-description"
              >
                <DialogTitle id="alert-dialog-title">Delete Skill?</DialogTitle>
                <DialogContent>
                  <DialogContentText id="alert-dialog-description">
                    Are you sure you want to remove this skill from your profile?
                  </DialogContentText>
                </DialogContent>
                <DialogActions>
                  <Button onClick={handleCloseDeleteConfirmation} color="primary">
                    Cancel
                  </Button>
                  <Button onClick={handleDelete} color="primary" autoFocus>
                    Yes
                  </Button>
                </DialogActions>
              </Dialog>
            </div>
          </CardActions>
        </Card>
      </Root>
    </>
  );
};
export default SkillSection;
