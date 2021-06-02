import React, { useContext, useState } from "react";

import { AppContext } from "../context/AppContext";
import { reportSkills } from "../api/memberskill.js";
import { getAvatarURL } from "../api/api.js";

import { render } from "react-dom";
import { ResponsiveRadar } from "@nivo/radar";

import PropTypes from "prop-types";
import { makeStyles, withStyles } from "@material-ui/core/styles";
import clsx from "clsx";
import Stepper from "@material-ui/core/Stepper";
import Step from "@material-ui/core/Step";
import StepLabel from "@material-ui/core/StepLabel";
import Check from "@material-ui/icons/Check";
import GroupAddIcon from "@material-ui/icons/GroupAdd";
import GroupIcon from "@material-ui/icons/Group";
import StepConnector from "@material-ui/core/StepConnector";

import "./TeamSkillReportPage.css";

import {
  selectOrderedSkills,
  selectCsrfToken,
  selectOrderedMemberProfiles,
  selectProfile,
  selectSkill,
} from "../context/selectors";

import {
  Avatar,
  Button,
  Card,
  CardHeader,
  Chip,
  List,
  ListItem,
  TextField,
  Typography,
} from "@material-ui/core";
import Autocomplete from "@material-ui/lab/Autocomplete";

import "./SkillReportPage.css";

const styles = {
  fontFamily: "sans-serif",
  textAlign: "center"
};


const useQontoStepIconStyles = makeStyles({
  root: {
    color: "#eaeaf0",
    display: "flex",
    height: 22,
    alignItems: "center"
  },
  active: {
    color: "#784af4"
  },
  circle: {
    width: 8,
    height: 8,
    borderRadius: "50%",
    backgroundColor: "currentColor"
  },
  completed: {
    color: "#784af4",
    zIndex: 1,
    fontSize: 18
  }
});


function QontoStepIcon(props) {
  const classes = useQontoStepIconStyles();
  const { active, completed } = props;

  return (
    <div
      className={clsx(classes.root, {
        [classes.active]: active
      })}
    >
      {completed ? (
        <Check className={classes.completed} />
      ) : (
        <div className={classes.circle} />
      )}
    </div>
  );
}

QontoStepIcon.propTypes = {
  /**
   * Whether this step is active.
   */
  active: PropTypes.bool,
  /**
   * Mark the step as completed. Is passed to child components.
   */
  completed: PropTypes.bool
};

const ColorlibConnector = withStyles({
  alternativeLabel: {
    top: 22
  },
  active: {
    "& $line": {
      backgroundImage:
        "linear-gradient( 95deg,rgb(242,113,33) 0%,rgb(233,64,87) 50%,rgb(138,35,135) 100%)"
    }
  },
  completed: {
    "& $line": {
      backgroundImage:
        "linear-gradient( 95deg,rgb(242,113,33) 0%,rgb(233,64,87) 50%,rgb(138,35,135) 100%)"
    }
  },
  line: {
    height: 3,
    border: 0,
    backgroundColor: "#eaeaf0",
    borderRadius: 1
  }
})(StepConnector);

const useColorlibStepIconStyles = makeStyles({
  root: {
    backgroundColor: "#ccc",
    zIndex: 1,
    color: "#fff",
    width: 50,
    height: 50,
    display: "flex",
    borderRadius: "50%",
    justifyContent: "center",
    alignItems: "center"
  },
  active: {
    backgroundImage:
      "linear-gradient( 136deg, rgb(242,113,33) 0%, rgb(233,64,87) 50%, rgb(138,35,135) 100%)",
    boxShadow: "0 4px 10px 0 rgba(0,0,0,.25)"
  },
  completed: {
    backgroundImage:
      "linear-gradient( 136deg, rgb(242,113,33) 0%, rgb(233,64,87) 50%, rgb(138,35,135) 100%)"
  }
});

function ColorlibStepIcon(props) {
  const classes = useColorlibStepIconStyles();
  const { active, completed } = props;
  const icons = {
    1: <GroupIcon />,
    2: <GroupAddIcon />
  };

  return (
    <div
      className={clsx(classes.root, {
        [classes.active]: active,
        [classes.completed]: completed
      })}
    >
      {icons[String(props.icon)]}
    </div>
  );
}

ColorlibStepIcon.propTypes = {
  /**
   * Whether this step is active.
   */
  active: PropTypes.bool,
  /**
   * Mark the step as completed. Is passed to child components.
   */
  completed: PropTypes.bool,
  /**
   * The label displayed in the step icon.
   */
  icon: PropTypes.node
};

const useStyles = makeStyles((theme) => ({
  root: {
    width: "100%"
  },
  button: {
    marginRight: theme.spacing(1)
  },
  instructions: {
    marginTop: theme.spacing(1),
    marginBottom: theme.spacing(1)
  }
}));

function getSteps() {
  return ["Select a Team", "Create an ad hoc Team"];
}

const SkillReportPage = (props) => {

  const classes = useStyles();
  const [activeStep, setActiveStep] = React.useState(0);
  const steps = getSteps();
  const [searchText, setSearchText] = useState("");

  const { state } = useContext(AppContext);
  const csrf = selectCsrfToken(state);
  const skills = selectOrderedSkills(state);
  const memberProfiles = selectOrderedMemberProfiles(state);
  const [searchResults, setSearchResults] = useState([]);
  const [searchRequestDTO] = useState([]);
  const [searchSkills, setSearchSkills] = useState([]);
  const [editedSearchRequest, setEditedSearchRequest] = useState(
    searchRequestDTO
  );

  const handleSearch = async (searchRequestDTO) => {
    let res = await reportSkills(searchRequestDTO, csrf);
    let memberSkillsFound;
    if (res && res.payload) {
      memberSkillsFound =
        res.payload.data.teamMembers && !res.error
          ? res.payload.data.teamMembers
          : undefined;
    }
    if (memberSkillsFound && memberProfiles) {
      setSearchResults(memberSkillsFound);
    } else {
      setSearchResults(undefined);
    }
  };

  function skillsToSkillLevelDTO(skills) {
    return skills.map((skill, index) => {
      let skillLevel = {
        id: skill.id,
        level: skill.skilllevel,
      };
      return skillLevel;
    });
  }

  function createRequestDTO(editedSearchRequest) {
    let skills = skillsToSkillLevelDTO(searchSkills);
    let members = [];
    let inclusive = false;
    let newSearchRequest = {
      skills: skills,
      members: members,
      inclusive: inclusive,
    };
    setEditedSearchRequest(newSearchRequest);
    return newSearchRequest;
  }

  function onSkillsChange(event, newValue) {
    let skillsCopy = newValue.sort((a, b) => a.name.localeCompare(b.name));
    setSearchSkills([...skillsCopy]);
  }

  const chip = (skill) => {
    let level = skill.level;
    let skillLevel = level.charAt(0) + level.slice(1).toLowerCase();
    let mappedSkill = selectSkill(state, skill.id);
    let chipLabel = mappedSkill.name + " - " + skillLevel;
    return <Chip label={chipLabel}></Chip>;
  };

const MyResponsiveRadar = ({ data }) => (
  <ResponsiveRadar
      data={data}
      keys={[ 'Michael', 'Jesse', 'Joe' ]}
      indexBy="skill"
      maxValue="auto"
      margin={{ top: 70, right: 80, bottom: 40, left: 80 }}
      curve="linearClosed"
      borderWidth={2}
      borderColor={{ from: 'color' }}
      gridLevels={5}
      gridShape="circular"
      gridLabelOffset={36}
      enableDots={true}
      dotSize={10}
      dotColor={{ theme: 'background' }}
      dotBorderWidth={2}
      dotBorderColor={{ from: 'color' }}
      enableDotLabel={true}
      dotLabel="value"
      dotLabelYOffset={-12}
      colors={{ scheme: 'nivo' }}
      fillOpacity={0.25}
      blendMode="multiply"
      animate={true}
      motionConfig="wobbly"
      isInteractive={true}
      legends={[
          {
              anchor: 'top-left',
              direction: 'column',
              translateX: -50,
              translateY: -40,
              itemWidth: 80,
              itemHeight: 20,
              itemTextColor: '#999',
              symbolSize: 12,
              symbolShape: 'circle',
              effects: [
                  {
                      on: 'hover',
                      style: {
                          itemTextColor: '#000'
                      }
                  }
              ]
          }
      ]}
  />
);

const data = [
{
  "skill": "Java",
  "Michael": 5,
  "Jesse": 3,
  "Joe": 4
},
{
  "skill": "JavaScript",
  "Michael": 4,
  "Jesse": 4,
  "Joe": 3
},
{
  "skill": "React",
  "Michael": 4,
  "Jesse": 4,
  "Joe": 3
},
{
  "skill": "GCP",
  "Michael": 3,
  "Jesse": 2,
  "Joe": 2
},
{
  "skill": "Micronaut",
  "Michael": 4,
  "Jesse": 2,
  "Joe": 4
}
];

  return (
    <div className="skills-report-page">
      <div className="and-members">
{/*         <Autocomplete */}
{/*           id="pdlSelect" */}
{/*           multiple */}
{/*           options={pdls} */}
{/*           value={selectedPdls || []} */}
{/*           onChange={onPdlChange} */}
{/*           getOptionLabel={(option) => option.name} */}
{/*           renderInput={(params) => ( */}
{/*             <TextField */}
{/*               {...params} */}
{/*               label="Select PDLs" */}
{/*               placeholder="Choose which PDLs to display" */}
{/*             /> */}
{/*           )} */}
{/*         /> */}
        <TextField
          label="Add Members"
          placeholder="Member Name"
          value={searchText}
          onChange={(e) => {
            setSearchText(e.target.value);
          }}
        />
      </div>
      <div className={classes.root}>
        <Stepper
          alternativeLabel
          activeStep={activeStep}
          connector={<ColorlibConnector />}
        >
          {steps.map((label) => (
            <Step key={label}>
              <StepLabel StepIconComponent={ColorlibStepIcon}>{label}</StepLabel>
            </Step>
          ))}
        </Stepper>
      </div>
      <div style={styles}>
{/*         <h1>Team Skill Radar Chart</h1> */}
        <div style={{ height: "400px" }}>
          <MyResponsiveRadar data={data} />
        </div>
      </div>
    </div>
  );
};

export default SkillReportPage;
