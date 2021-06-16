import "./TemplateCard.css";
import React, {useState} from "react";
import Card from '@material-ui/core/Card';
import CardContent from '@material-ui/core/CardContent';
import { makeStyles } from '@material-ui/core/styles';
import clsx from 'clsx';
import CardActions from '@material-ui/core/CardActions';
import Fullscreen from '@material-ui/icons/Fullscreen';
import FullscreenExit from '@material-ui/icons/FullscreenExit';
import Dialog from '@material-ui/core/Dialog';
import ListItemText from '@material-ui/core/ListItemText';
import ListItem from '@material-ui/core/ListItem';
import List from '@material-ui/core/List';
import Divider from '@material-ui/core/Divider';
import AppBar from '@material-ui/core/AppBar';
import Toolbar from '@material-ui/core/Toolbar';
import IconButton from '@material-ui/core/IconButton';
import Typography from '@material-ui/core/Typography';
import CloseIcon from '@material-ui/icons/Close';
import Slide from '@material-ui/core/Slide';

const useStyles = makeStyles((theme) => ({
    root: {
        maxWidth: 1000,
    },
    media: {
        height: 0,
    },
    expand: {
        justifyContent: "right",
        transition: theme.transitions.create('transform', {
            duration: theme.transitions.duration.shortest,
        }),
    },
    expandOpen: {
        justifyContent: "right",
    }
}));

const Transition = React.forwardRef(function Transition(props, ref) {
    return <Slide direction="up" ref={ref} {...props} />;
});

const TemplateCard = ({templateName = "Ad Hoc", description = "Ask a single question", creator = "Admin"}) => {
    const classes = useStyles();

    const [expanded, setExpanded] = useState(false);

    const handleExpandClick = () => {
        setExpanded(!expanded);
    }

    return (
        <Card className={classes.root}
              className = 'feedback-template-card'>
            <CardActions disableSpacing>
                <IconButton
                    className={clsx(classes.expand, {
                        [classes.expandOpen]: expanded,
                    })}
                    onClick={handleExpandClick}
                    aria-expanded={expanded}
                    aria-label="show more"
                >
                    {!expanded ? <Fullscreen /> : <FullscreenExit/>}

                </IconButton>
            </CardActions>
            <CardContent>
                <div className='card-content'>
                    <div className='templateName' >
                        {templateName}
                    </div>
                    <div className='description-and-creator'>
                        <div className='description'>
                            {description}
                        </div>

                        <div className='creator-wrapper'>
                            Created by:
                            <div className='creator'>
                                {creator}
                            </div>
                        </div>
                    </div>
                </div>

            </CardContent>
            <Dialog fullScreen open={expanded} onClose={handleExpandClick} TransitionComponent={Transition}>
                <AppBar className={classes.appBar}>
                    <Toolbar>
                        <IconButton edge="start" color="inherit" onClick={handleExpandClick} aria-label="close">
                            <CloseIcon />
                        </IconButton>
                        <div className={classes.title}>
                            <Typography variant="h6">
                                Peer Feedback
                            </Typography>
                            <Typography variant="subtitle2">
                                A short and simple survey targeting strengths and opportunities
                            </Typography>
                        </div>
                    </Toolbar>
                </AppBar>
                <List>
                    <ListItem button>
                        <ListItemText primary="Question 1" secondary="What are this team member's top strengths (include examples where possible)?" />
                    </ListItem>
                    <Divider />
                    <ListItem button>
                        <ListItemText primary="Question 2" secondary="In what ways are this team member’s contributions impacting the objectives of the organization, their project, or their team?" />
                    </ListItem>
                    <Divider />
                    <ListItem button>
                        <ListItemText primary="Question 3" secondary="What can this team member improve on that would help them increase their effectiveness (include examples where possible)?" />
                    </ListItem>
                    <Divider />
                    <ListItem button>
                        <ListItemText primary="Question 4" secondary="In what ways does this team member represent OCI's values? (OCI Values: All of us is better than any one of us, Innovate courageously, Share it back)" />
                    </ListItem>
                </List>
            </Dialog>
        </Card>
    );

}

export default TemplateCard;