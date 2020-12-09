import React, { useContext, useEffect, useState } from "react";
import { AppContext, selectPendingSkills } from "../../context/AppContext";

import Autocomplete from "@material-ui/lab/Autocomplete";
import { Checkbox, Modal, TextField } from "@material-ui/core";
import CheckBoxOutlineBlankIcon from "@material-ui/icons/CheckBoxOutlineBlank";
import CheckBoxIcon from "@material-ui/icons/CheckBox";

import "./PendingSkills.css";

const icon = <CheckBoxOutlineBlankIcon fontSize="small" />;
const checkedIcon = <CheckBoxIcon fontSize="small" />;

const CombineSkillsModal = ({ open, onClose }) => {
  const { state } = useContext(AppContext);
  const { skills } = state;

  const [pendingSkills, setPendingSkills] = useState([]);
  // const [skillsoChange, setSkillsToChange] = useState([]);
  const [editedSkill, setEditedSkill] = useState({
    name: "",
    description: "",
    id: "",
  });
  const [hasEdited, setHasEdited] = useState(false);

  useEffect(() => {
    setPendingSkills(selectPendingSkills(state));
  }, [skills, state]);

  const handleSelections = (event, values) => {
    // setSkillsToChange(values);
    if (!hasEdited) {
      setEditedSkill(values[0]);
    }
  };

  const close = () => {
    onClose();
    setHasEdited(false);
    setEditedSkill({
      name: "",
      description: "",
      id: "",
    });
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
              setEditedSkill({ ...editedSkill, name: e.target.value });
            }}
            value={editedSkill ? editedSkill.name : ""}
            variant="outlined"
          />
          <TextField
            className="halfWidth"
            label="Description"
            multiline
            onChange={(e) => {
              setHasEdited(true);
              setEditedSkill({ ...editedSkill, description: e.target.value });
            }}
            value={editedSkill ? editedSkill.description : ""}
            variant="outlined"
          />
        </div>
      )}
    </Modal>
  );
};

export default CombineSkillsModal;
