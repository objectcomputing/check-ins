import "./TemplateCard.css";
import React from "react";
import Card from '@material-ui/core/Card';
import CardContent from '@material-ui/core/CardContent';
import { makeStyles } from '@material-ui/core/styles';
import clsx from 'clsx';
import CardActions from '@material-ui/core/CardActions';
import Fullscreen from '@material-ui/icons/Fullscreen';
import FullscreenExit from '@material-ui/icons/FullscreenExit';
import PropTypes from "prop-types";
import IconButton from '@material-ui/core/IconButton';
import VisibilityIcon from '@material-ui/icons/Visibility';

import "./TemplateCard.css"

const useStyles = makeStyles((theme) => ({
    root: {
        maxWidth: 1000,
    },
    appBar: {
        position: 'relative',
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

const propTypes = {
    title: PropTypes.string.isRequired,
    description: PropTypes.string,
    creator: PropTypes.string.isRequired,
    isAdHoc: PropTypes.bool,
    onClick: PropTypes.func,
    onCardClick: PropTypes.func
}

const cutText = (text, maxCharacters) => {
    if (!text) {
        text = "";
    }
    let shortenedText = text;
    if (text.length > maxCharacters) {
        shortenedText = `${text.substring(0, maxCharacters)}...`;
    }
    return shortenedText;
}

const TemplateCard = (props) => {

    const classes = useStyles();

    const handleClick = (e) => {
        e.stopPropagation();
        props.onClick(e);
    }

    return (
        <Card onClick={props.onCardClick} className='feedback-template-card'>
            <CardActions className="card-actions" disableSpacing>
                {!props.isAdHoc &&
                <IconButton
                  className={clsx(classes.expand, {
                    [classes.expandOpen]: props.expanded,
                  })}
                  onClick={(e) => handleClick(e)}
                  aria-expanded={props.expanded}
                  aria-label="show more">
                <VisibilityIcon>
                    {!props.expanded ? <Fullscreen/> : <FullscreenExit/>}
                </VisibilityIcon>
                </IconButton>
                }
            </CardActions>
            <CardContent className="card-content">
                <div className="template-details">
                    <h3 className="template-name">{cutText(props.title, 20)}</h3>
                    <p className="description">{cutText(props.description, 90)}</p>
                </div>
                <p className="creator">Created by: <b>{props.creator}</b></p>
            </CardContent>
        </Card>
    );

};

TemplateCard.propTypes = propTypes;

export default TemplateCard;