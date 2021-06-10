import React from "react";
import Typography from "@material-ui/core/Typography";
import { makeStyles } from '@material-ui/core/styles';
import Card from '@material-ui/core/Card';
import CardContent from '@material-ui/core/CardContent';
import AvatarComponent from '../avatar/Avatar';
import Divider from '@material-ui/core/Divider';


const useStyles = makeStyles({
  root: {
    minWidth: '10em',
    maxWidth: '20em',

  },
  cardContent: {
    display: 'flex',
    alignItems: 'center',
    alignContent: 'center',
    flexDirection: 'column',
    justifyContent:'center',
    textAlign: 'center',
  },

  recommendationText: {
    color: "gray",

  },
  divider: {
    backgroundColor:"#3f51b5",
    width: '100%',
    marginBottom: '1em',
  },

  bullet: {
    display: 'inline-block',
    margin: '0 2px',
    transform: 'scale(0.8)',
  },

  title: {
    fontSize: 16,
  },

  pos: {
    marginTop: '0.3em',
    marginBottom: '0.5em',
    fontSize: 20,
    color: 'black'
  },
});


const FeedbackRecipientCard = () => {
  const classes = useStyles();
  const name = "Slim Jim"
  const reason = "Recommended for being a local opossum"
  return (
    <Card className={classes.root}>
      <CardContent className={classes.cardContent}>
        <AvatarComponent id="avatar-pic" name="avatar-pic" src='../../../public/default_profile.jpg'></AvatarComponent>
        <Typography id="name" name="name" className={classes.pos} color="textSecondary">
          {name}
        </Typography>
        <Divider variant="middle" className={classes.divider} />
        <Typography id="rec_reason" name="rec_reason" component="p" className={classes.recommendationText}>
          {reason}
          <br />
        </Typography>
      </CardContent>
    </Card>
  );
}
export default FeedbackRecipientCard;



