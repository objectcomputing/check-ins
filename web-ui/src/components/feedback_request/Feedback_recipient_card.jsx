import React, {useContext} from "react";
import Typography from "@material-ui/core/Typography";
import {makeStyles, withStyles} from '@material-ui/core/styles';
import Card from '@material-ui/core/Card';
import CardContent from '@material-ui/core/CardContent';
import Avatar from '@material-ui/core/Avatar';
import Divider from '@material-ui/core/Divider';
import {selectProfile} from "../../context/selectors";
import {AppContext} from "../../context/AppContext";
import {getAvatarURL} from "../../api/api";
import {CardHeader} from "@material-ui/core";
import {green} from "@material-ui/core/colors";
import CheckCircleIcon from "@material-ui/icons/CheckCircle";


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

const brandCardHeaderStyles = ({ palette, breakpoints }) => {
  const space = 24;
  return {
    root: {
      minWidth: 256,
    },
    header: {
      padding: `4px ${space}px 0`,
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'space-between',
    },
    avatar: {
      width: 48,
      height: 48,
      transform: 'translateY(50%)',
      '& > img': {
        margin: 0,
        backgroundColor: palette.common.white,
      },
      [breakpoints.up('sm')]: {
        width: 60,
        height: 60,
      },
    },
    divider: {
      backgroundColor: palette.grey[200],
      marginBottom: 24 - 1, // minus 1 due to divider height
      [breakpoints.up('sm')]: {
        marginBottom: 30 - 1, // minus 1 due to divider height
      },
    },
    extra: {
      textTransform: 'uppercase',
      fontSize: 14,
      color: palette.grey[500],
      letterSpacing: '1px',
    },
  };
};

const BrandCardHeader = withStyles(brandCardHeaderStyles, {
  name: 'BrandCardHeader',
})(({ classes, image, selected }) => (
  <div className={classes.root}>
    <div className={classes.header}>
      <Avatar alt={'brand logo'} className={classes.avatar} src={image} />
      {selected && (<CheckCircleIcon style={{ color: green[500]}}>checkmark-image</CheckCircleIcon>)}
    </div>
    <hr className={classes.divider} />
  </div>
));

const FeedbackRecipientCard = ({profileId, reason, selected=false, onClick}) => {
  const classes = useStyles();
  const {state} = useContext(AppContext);
  const recipientProfile = selectProfile(state, profileId);

  return (
    <Card className={classes.root} onClick={onClick}>
      <CardHeader component={BrandCardHeader} image={getAvatarURL(recipientProfile?.workEmail)} selected={selected}/>
      <CardContent className={classes.cardContent}>
        <Typography id="name" name="name" className={classes.pos} color="textSecondary">
          {recipientProfile?.name}
        </Typography>
        <Typography id="title" name="title" variant="subtitle1">
          {recipientProfile?.title}
        </Typography>
        {reason &&
        (<React.Fragment>
          <Divider variant="middle" className={classes.divider}/>
          <Typography id="rec_reason" name="rec_reason" component="p" className={classes.recommendationText}>
            {reason}
          </Typography>
        </React.Fragment>)}
      </CardContent>
    </Card>
  );
}
export default FeedbackRecipientCard;



