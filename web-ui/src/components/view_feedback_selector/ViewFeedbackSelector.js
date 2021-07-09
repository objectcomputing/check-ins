import React from 'react';
import { makeStyles } from '@material-ui/core/styles';
import Card from '@material-ui/core/Card';
import { Typography } from '@material-ui/core';
import CardContent from '@material-ui/core/CardContent';
import CardActions from '@material-ui/core/CardActions';
import AvatarComponent from '../avatar/Avatar';
import Collapse from '@material-ui/core/Collapse';
import ExpandMoreIcon from '@material-ui/icons/ExpandMore';
import "./ViewFeedbackSelector.css"
import IconButton from '@material-ui/core/IconButton';
import Grid from '@material-ui/core/Grid';
import { Link } from "react-router-dom";
import Divider from '@material-ui/core/Divider';


const useStyles = makeStyles({

  root: {
    color: "gray",
    maxWidth: "80%",
    width: "60%",
    maxHeight: "10%",
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
    marginLeft: 'auto',
    transition: "transform 0.1s linear",
  },
  expandOpen: {
    transform: 'rotate(180deg)',
    transition: "transform 0.1s linear",
    marginLeft: 'auto',
  },
});

const useStylesCardContent = makeStyles({
  root: {
    paddingBottom: 0,
    paddingTop: 0,
    '&:last-child': {
      paddingBottom: 0,
    }
  }
}, { name: "MuiCardContent" })

const useStylesCardActions = makeStyles({
  root: {
    padding: 0,
    maxHeight: "30px",
  },

}, { name: 'MuiCardActions' })

const useStylesText = makeStyles({
  body1: {
    ['@media (max-width:767px)']: { // eslint-disable-line no-useless-computed-key
      fontSize: "0.7rem",
    },
  }
}, { name: "MuiTypography" })


const ViewFeedbackSelector = () => {
  const classes = useStyles();
  useStylesCardActions();
  useStylesText();
  useStylesCardContent();
  const [expanded, setExpanded] = React.useState(false);

  const handleExpandClick = () => {
    setExpanded(!expanded);
  };

  return (
    <div className="input-row">

      {/* //every requestee gets mapped to this part of the component */}
      <Card className={classes.root}>
        <div className="has-padding-top">
          <CardContent className={classes.noBottomPadding}>
            <Grid container spacing={0}>
              <Grid item xs={12}>
                <Grid container
                  direction="row"
                  alignItems="center"
                  className="no-wrap"
                >
                  <Grid item>
                    <AvatarComponent imageUrl="../../../public/default_profile.jpg"></AvatarComponent>
                  </Grid>
                  <Grid item xs className="small-margin">
                    <Typography className="person-name" >Slim Jim</Typography>
                    <Typography className="position-text">Resident Animal Ambassador</Typography>
                  </Grid>
                  <Grid item xs={4} className="align-end">
                    <Typography className="dark-gray-text">Dev Template 1</Typography>
                    <Link to="" className="response-link red-text">View all responses</Link>
                  </Grid>
                </Grid>
              </Grid>
            </Grid>


          </CardContent>
        </div>
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
            <Divider className="person-divider"></Divider>

            {/* //every person underneath the PDL requestee is mapped to this part of this component */}
            <Grid container spacing={6} className="person-row">
              <Grid item xs={12}>
                <Grid container
                  direction="row"
                  alignItems="center"
                  className="no-wrap"
                >
                  <Grid item>
                    <AvatarComponent imageUrl="../../../public/default_profile.jpg"></AvatarComponent>
                  </Grid>
                  <Grid item xs className="small-margin">
                    <Typography className="person-name" >Chip Dip</Typography>
                    <Typography className="position-text">Senior Engineer</Typography>
                  </Grid>
                  <Grid item xs={4} className="align-end">
                    <Typography >Submitted 5/22</Typography>
                    <Link to="" className="response-link">View response</Link>

                  </Grid>
                </Grid>
              </Grid>
            </Grid>


            <Divider className="person-divider" ></Divider>
            <Grid container spacing={6} className="person-row" >
              <Grid item xs={12} >
                <Grid container
                  direction="row"
                  alignItems="center"
                  className="no-wrap"
                >
                  <Grid item>
                    <AvatarComponent imageUrl="../../../public/default_profile.jpg"></AvatarComponent>
                  </Grid>
                  <Grid item xs className="small-margin">
                    <Typography className="person-name" >Erin Smith</Typography>
                    <Typography className="position-text">External Colleague</Typography>
                  </Grid>
                  <Grid item xs={4} className="align-end">
                    <Typography >Submitted 5/23</Typography>
                    <Link to = " " className="response-link">View response</Link>
                  </Grid>
                </Grid>
              </Grid>
            </Grid>
          </CardContent>
        </Collapse>
      </Card>
    </div>


  )
}
export default ViewFeedbackSelector;
