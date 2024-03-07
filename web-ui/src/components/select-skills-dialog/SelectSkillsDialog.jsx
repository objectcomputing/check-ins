import React, {useCallback, useState} from "react";
import {
  AppBar,
  Checkbox, Dialog,
  DialogContent,
  FormGroup,
  IconButton, List, ListItem, ListItemButton, ListItemText,
  Slide,
  TextField,
  Toolbar,
  Typography
} from "@mui/material";
import PropTypes from "prop-types";
import {Close, Search} from "@mui/icons-material";
import InputAdornment from "@mui/material/InputAdornment";
import FormControlLabel from "@mui/material/FormControlLabel";

const Transition = React.forwardRef(function Transition(props, ref) {
  return <Slide direction="up" ref={ref} {...props} />;
});

const propTypes = {
  isOpen: PropTypes.bool.isRequired,
  onClose: PropTypes.func.isRequired,
  selectableSkills: PropTypes.arrayOf(PropTypes.object)
};

const SelectSkillsDialog = ({ isOpen, onClose, selectableSkills }) => {

  const [query, setQuery] = useState("");
  const [showPendingSkills, setShowPendingSkills] = useState(false);

  const reset = () => {
    setQuery("");
  }

  const getFilteredSkills = useCallback(() => {
    if (selectableSkills) {
      return selectableSkills.filter(skill => {
        const sanitizedQuery = query.toLowerCase().trim();
        const nameMatches = skill.name.toLowerCase().includes(sanitizedQuery);
        if (showPendingSkills) {
          return nameMatches;
        } else {
          return nameMatches && !skill.pending;
        }
      });
    }

    return [];
  }, [query, selectableSkills, showPendingSkills]);

  return (
    <Dialog
      open={isOpen}
      fullScreen
      onClose={() => {
        reset();
        onClose();
      }}
      TransitionComponent={Transition}>
      <AppBar>
        <Toolbar>
          <IconButton
            edge="start"
            color="inherit"
            onClick={() => {
              reset();
              onClose();
            }}
          ><Close/></IconButton>
          <Typography variant="h6">Add Skills to Category</Typography>
        </Toolbar>
      </AppBar>
      <DialogContent style={{ margin: "5rem 1rem 1rem 1rem" }}>
        <FormGroup row style={{ display: "flex", justifyContent: "space-between" }}>
          <TextField
            style={{ minWidth: "200px" }}
            label="Search"
            placeholder="Skill name"
            variant="outlined"
            value={query}
            onChange={(event) => setQuery(event.target.value)}
            InputProps={{
              endAdornment: <InputAdornment position="end" color="gray"><Search/></InputAdornment>
            }}
          />
          <FormControlLabel
            control={<Checkbox
              checked={showPendingSkills}
              onChange={(event) => setShowPendingSkills(event.target.checked)}
            />}
            label="Show Pending Skills">
          </FormControlLabel>
        </FormGroup>
        <List dense role="list">
          {getFilteredSkills().map(skill => (
            <ListItem
              key={skill.id}
              role="listitem"
              secondaryAction={
                <Checkbox
                  disableRipple
                />
              }
            >
              <ListItemButton>
                <ListItemText primary={skill.name} secondary={skill.description}/>
              </ListItemButton>
            </ListItem>
          ))}
        </List>
      </DialogContent>
    </Dialog>
  );
};

SelectSkillsDialog.propTypes = propTypes;

export default SelectSkillsDialog;