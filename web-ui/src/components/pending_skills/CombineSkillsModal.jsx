import React, { useContext, useEffect, useState } from "react";
import { AppContext, selectPendingSkills, COMBINE_SKILLS } from "../../context/AppContext";
import { combineSkill } from "../../api/skill";

import Autocomplete from "@material-ui/lab/Autocomplete";
import { Checkbox, Modal, TextField } from "@material-ui/core";
import { Button } from "@material-ui/core";
import CheckBoxOutlineBlankIcon from "@material-ui/icons/CheckBoxOutlineBlank";
import CheckBoxIcon from "@material-ui/icons/CheckBox";

import "./PendingSkills.css";

const icon = <CheckBoxOutlineBlankIcon fontSize="small" />;
const checkedIcon = <CheckBoxIcon fontSize="small" />;

const CombineSkillsModal = ({ open, onClose }) => {
const { state, dispatch } = useContext(AppContext);
const { skills, csrf } = state;
const [pendingSkills, setPendingSkills] = useState([]);
const [combinedSkill, setCombinedSkill] = useState({
    name: "",
    description: "",
    skillsToCombine: [],
  });
const { skillsToCombine, name } = combinedSkill;
const [hasEdited, setHasEdited] = useState(false);

useEffect(() => {
setPendingSkills(selectPendingSkills(state));
}, [skills, state]);

const handleSelections = (event, values) => {
  let skills = values.map((skill) => { return skill.id})
    setCombinedSkill({
      ...combinedSkill,
        skillsToCombine: skills,
    });
};

const close = () => {
  onClose();
  setHasEdited(false);
  setCombinedSkill({
    name: "",
    description: "",
    skillsToCombine: "",
  });
};

const save  = async () => {
  if (name && skillsToCombine && csrf)
    {
    const res = await combineSkill({ name: combinedSkill.name,description: combinedSkill.description, skillsToCombine: combinedSkill.skillsToCombine, pending: true }, csrf);
    const data =
        res && res.payload && res.payload.data ? res.payload.data : null;
    if (!data) {
      return;
    }
    dispatch({ type: COMBINE_SKILLS, payload: data });
    setCombinedSkill(data);
    close();
  }
};

return (
    <Modal open={open} onClose={close}>
      {pendingSkills && (
        <div className="combine-skills-modal">
          <Autocomplete
            multiple
            options={pendingSkills}
            disableCloseOnSelect
            getOptionLabel={(option) => option.name}
            onChange={handleSelections}
            renderOption={(option, { selected }) => (
              <React.Fragment>
                <Checkbox
                  icon={icon}
                  checkedIcon={checkedIcon}
                  style={{ marginRight: 8 }}
                  checked={selected}
                />
                {option.name}
              </React.Fragment>
            )}
            style={{ width: 500 }}
            renderInput={(params) => (
              <TextField
                {...params}
                label="Choose Skills to Combine"
                variant="outlined"
              />
            )}
          />
          <TextField
            className="halfWidth"
            label="Name"
            onChange={(e) => {
              setHasEdited(true);
              setCombinedSkill({ ...combinedSkill, name: e.target.value });
            }}
            value={combinedSkill ? combinedSkill.name : ""}
            variant="outlined"
          />
          <TextField
            className="halfWidth"
            label="Description"
            multiline
            onChange={(e) => {
              setHasEdited(true);
              setCombinedSkill({ ...combinedSkill, description: e.target.value });
            }}
            value={combinedSkill ? combinedSkill.description : ""}
            variant="outlined"
          />
          <div className="fullWidth">
          <Button onClick={close} color="secondary">
            Cancel
            </Button>
            <Button
            onClick={save} color="primary">
            Save Team
          </Button>
          </div>
          </div>
      )}
    </Modal>
  );
};
export default CombineSkillsModal;