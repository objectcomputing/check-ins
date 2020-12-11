import React, { useContext, useEffect, useState } from "react";
import { AppContext } from "../../context/AppContext";

import Autocomplete from "@material-ui/lab/Autocomplete";
import { Checkbox, Modal, TextField } from "@material-ui/core";
import CheckBoxOutlineBlankIcon from "@material-ui/icons/CheckBoxOutlineBlank";
import CheckBoxIcon from "@material-ui/icons/CheckBox";

import "./PendingSkillsModal.css";

const icon = <CheckBoxOutlineBlankIcon fontSize="small" />;
const checkedIcon = <CheckBoxIcon fontSize="small" />;

const EditPendingSkillsModal = ({ open, onClose }) => {
  const { state } = useContext(AppContext);
  const { skills } = state;

  const [pendingSkills, setPendingSkills] = useState([]);
  const [skillsToChange, setSkillsToChange] = useState([]);
  const [editedSkill, setEditedSkill] = useState({
    name: "",
    description: "",
    id: "",
  });

  //to avoid eslint issues until modal story is complete
  console.log({ skillsToChange });

  useEffect(() => {
    setPendingSkills(skills);
  }, [skills]);

  const handleSelections = (event, values) => {
    setSkillsToChange(values);
    setEditedSkill(values[0]);
  };

  return (
    <Modal open={open} onClose={onClose}>
      {pendingSkills && (
        <div className="EditPendingSkillsModal">
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
            onChange={(e) =>
              setEditedSkill({ ...editedSkill, name: e.target.value })
            }
            value={editedSkill ? editedSkill.name : ""}
            variant="outlined"
          />
          <TextField
            className="halfWidth"
            label="Description"
            multiline
            onChange={(e) =>
              setEditedSkill({ ...editedSkill, description: e.target.value })
            }
            value={editedSkill ? editedSkill.description : ""}
            variant="outlined"
          />
        </div>
      )}
    </Modal>
  );
};

export default EditPendingSkillsModal;
