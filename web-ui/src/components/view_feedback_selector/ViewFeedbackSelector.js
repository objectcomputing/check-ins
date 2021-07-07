import React from 'react';
import { makeStyles } from '@material-ui/core/styles';
import InputAdornment from "@material-ui/core/InputAdornment";
import Search from "@material-ui/icons/Search";
import InputLabel from '@material-ui/core/InputLabel';
import MenuItem from '@material-ui/core/MenuItem';
import FormControl from '@material-ui/core/FormControl';
import Select from '@material-ui/core/Select';
import TextField from "@material-ui/core/TextField";
import "./ViewFeedbackSelector.css"

const useStyles = makeStyles({
  root: {
    color: "gray"
  },
  textField: {
    width: "20%",
    ['@media (max-width:767px)']: { // eslint-disable-line no-useless-computed-key
      width: '40%',
      marginRight: "1em"
    },
    marginRight: "3em",
    marginTop: "1.15em"
  },
  formControl: {
    marginRight: "1em",
    width: "20%",

  },
  selectEmpty: {
  },
});


const ViewFeedbackSelector = () => {
  const classes = useStyles();




  return (
    <div className="view-feedback-selector-container">
    <div className="row two-em-top-margin two-em-left-margin align-end">
        <TextField
          className={classes.textField}
          placeholder="Search..."
          InputProps={{
            startAdornment: (
              <InputAdornment className={classes.root} position="start">
                <Search />
              </InputAdornment>
            ),
          }}
        />
        <FormControl className={classes.formControl}>
          <InputLabel shrink id="select-time-label">
            Filter by
          </InputLabel>
          <Select
            labelId="select-time-label"
            id="select-time"
          // value={age}
          // onChange={handleChange}
          >
            <MenuItem value={"Past 3"}>Past 3 months</MenuItem>
            <MenuItem value={"All time"}>All time</MenuItem>
            <MenuItem value={"Past 6"}>Past 6 months</MenuItem>
            <MenuItem value={"Past Year"}>Past year</MenuItem>
          </Select>
        </FormControl>

        <FormControl className={classes.formControl}>
          <InputLabel shrink id="select-sort-method-label">
            Sort by
          </InputLabel>
          <Select
            labelId="select-sort-method-label"
            id="select-sort-method"
          // value={age}
          // onChange={handleChange}
          >
            <MenuItem value={"Requested"}>Submission date</MenuItem>
            <MenuItem value={"Submission"}>Request sent date</MenuItem>
          </Select>
        </FormControl>
        </div>
      </div>
  )
}
export default ViewFeedbackSelector;
