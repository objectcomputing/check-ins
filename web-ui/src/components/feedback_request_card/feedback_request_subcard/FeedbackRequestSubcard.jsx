import React from "react";
import PropTypes from "prop-types";
import Grid from "@material-ui/core/Grid";
import AvatarComponent from "../../avatar/Avatar";
import Typography from "@material-ui/core/Typography";
import {Link} from "react-router-dom";
import Divider from "@material-ui/core/Divider";

const propTypes = {
  recipientName: PropTypes.string.isRequired,
  recipientTitle: PropTypes.string.isRequired,
}

const FeedbackRequestSubcard = (props) => {

  return (
    <React.Fragment>
      <Divider className="person-divider"/>
      <Grid container spacing={6} className="person-row">
        <Grid item xs={12}>
          <Grid
            container
            direction="row"
            alignItems="center"
            className="no-wrap">
            <Grid item>
              <AvatarComponent imageUrl="../../../public/default_profile.jpg"/>
            </Grid>
            <Grid item xs className="small-margin">
              <Typography className="person-name">{props.recipientName}</Typography>
              <Typography className="position-text">{props.recipientTitle}</Typography>
            </Grid>
            <Grid item xs={4} className="align-end">
              <Typography>Submitted 5/22</Typography>
              <Link to="" className="response-link">View response</Link>
            </Grid>
          </Grid>
        </Grid>
      </Grid>
    </React.Fragment>
  );
}

FeedbackRequestSubcard.propTypes = propTypes;

export default FeedbackRequestSubcard;