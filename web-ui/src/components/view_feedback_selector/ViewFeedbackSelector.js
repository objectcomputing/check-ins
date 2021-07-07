import React, { useState } from 'react';
import { makeStyles } from '@material-ui/core/styles';
import InputAdornment from "@material-ui/core/InputAdornment";
import Search from "@material-ui/icons/Search";
import InputLabel from '@material-ui/core/InputLabel';
import MenuItem from '@material-ui/core/MenuItem';
import FormControl from '@material-ui/core/FormControl';
import Select from '@material-ui/core/Select';
import TextField from "@material-ui/core/TextField";
import Card from '@material-ui/core/Card';
import CardHeader from '@material-ui/core/CardHeader';
import CardMedia from '@material-ui/core/CardMedia';
import { Typography } from '@material-ui/core';
import CardContent from '@material-ui/core/CardContent';
import CardActions from '@material-ui/core/CardActions';
import AvatarComponent from '../avatar/Avatar';
import Collapse from '@material-ui/core/Collapse';
import ExpandMoreIcon from '@material-ui/icons/ExpandMore';
import "./ViewFeedbackSelector.css"
import IconButton from '@material-ui/core/IconButton';
import Grid from '@material-ui/core/Grid';


const useStyles = makeStyles({
  root: {
    color: "gray",
    maxWidth: "80%",
    width: "60%",
    maxHeight: "15%",
    ['@media (max-width:769px)']: { // eslint-disable-line no-useless-computed-key
      width: '100%',
      maxWidth: '100%',
    },
  },

  textField: {
    width: "15%",
    ['@media (max-width:769px)']: { // eslint-disable-line no-useless-computed-key
      width: '40%',
    },
    marginTop: "1.15em",
    marginRight: "3em",
  },
  formControl: {
    marginRight: "1em",

  },
  expandClose: {
    transform: 'rotate(0deg)',
    marginRight: 'auto',
  },
  expandOpen: {
    transform: 'rotate(180deg)',
    marginRight: 'auto',
  },
});

const useStylesCardHeader = makeStyles({
  action: {
    paddingTop: "0.7em",
    paddingRight: "1em",
  }

}, { name: 'MuiCardHeader' })

const useStylesCardActions = makeStyles({
  root: {
    padding: 0,
    maxHeight: "30px",
  },

}, { name: 'MuiCardActions' })

const useStylesText = makeStyles({
  body1: {
    ['@media (max-width:767px)']: { // eslint-disable-line no-useless-computed-key
      fontSize:"0.7rem",
    },
  }
}, {name:  "MuiTypography"})
const ViewFeedbackSelector = () => {
  const classes = useStyles();
  const headerClass = useStylesCardHeader();
  const actionClass = useStylesCardActions();
  const textClass = useStylesText();
  const [expanded, setExpanded] = React.useState(false);

  const handleExpandClick = () => {
    setExpanded(!expanded);
  };

  return (
    <React.Fragment>
      <div className="input-row">
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

        <FormControl>
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

      <div className="input-row">
        <Card className={classes.root}>
          <CardHeader
            avatar={
              <AvatarComponent imageUrl="../../../public/default_profile.jpg"></AvatarComponent>
            }
            title={
              <Typography className="person-name">
                Slim Jim
              </Typography>
            }

            subheader= {
              <Typography>Resident Animal Ambassador</Typography>
            }
      

            action={
              <div className="card-info-container">
                <Typography >Dev Template 1</Typography>
                <Typography className="red-text"> View 3/5 responses</Typography>
              </div>
            }

          >


          </CardHeader>
          <CardActions disableSpacing>
            <IconButton
              onClick={handleExpandClick}
              aria-expanded={expanded}
              aria-label="show more"
              className={expanded ? classes.expandOpen : classes.expandClose}
            >
              <ExpandMoreIcon />
            </IconButton>
          </CardActions>
          <Collapse in={expanded} timeout="auto" unmountOnExit>
            <CardContent>
              <Grid container spacing={0}>
                <Grid item xs={12}>
                  <Grid container 
                    direction="row"
                    justifyContent="center"
                    alignItems="center"
                    className="no-wrap"
                  >
                    <Grid item>
                    <AvatarComponent imageUrl="../../../public/default_profile.jpg"></AvatarComponent>
                    </Grid>
                    <Grid item xs className="small-margin">
                    <Typography >Chip Dip</Typography>
                    <Typography>Senior Engineer</Typography>
                    </Grid>
                    <Grid item xs={4} className="align-end">
                      <Typography className="response-link">View response</Typography>
                  
                    </Grid>
                  </Grid>
                </Grid>
              </Grid>
            </CardContent>
          </Collapse>
        </Card>

      </div>
    </React.Fragment>


  )
}
export default ViewFeedbackSelector;
