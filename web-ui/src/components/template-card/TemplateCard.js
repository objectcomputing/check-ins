import "./TemplateCard.css";
import IconButton from '@material-ui/core/IconButton';
import React, {useState} from "react";
import Card from '@material-ui/core/Card';
import Collapse from '@material-ui/core/Collapse';
import CardContent from '@material-ui/core/CardContent';
import { makeStyles } from '@material-ui/core/styles';
import clsx from 'clsx';
import CardHeader from '@material-ui/core/CardHeader';
import CardMedia from '@material-ui/core/CardMedia';
import CardActions from '@material-ui/core/CardActions';
import Avatar from '@material-ui/core/Avatar';
import Typography from '@material-ui/core/Typography';
import { red } from '@material-ui/core/colors';
import FavoriteIcon from '@material-ui/icons/Favorite';
import ShareIcon from '@material-ui/icons/Share';
import Fullscreen from '@material-ui/icons/Fullscreen';
import FullscreenExit from '@material-ui/icons/FullscreenExit';
import MoreVertIcon from '@material-ui/icons/MoreVert';

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

const TemplateCard = () => {
    const classes = useStyles();
    const templateName = "Ad Hoc"
    const description = "Ask a single question"
    const creator = "Admin"


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
            <Collapse in={expanded} timeout="auto" unmountOnExit>
                <CardContent>
                    asdfasdf
                </CardContent>
            </Collapse>
        </Card>
    );

}

export default TemplateCard;