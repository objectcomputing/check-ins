import "./TemplateCard.css";
import React from "react";
import Card from '@material-ui/core/Card';
import CardContent from '@material-ui/core/CardContent';
import PropTypes from "prop-types";
import IconButton from '@material-ui/core/IconButton';
import VisibilityIcon from '@material-ui/icons/Visibility';

import "./TemplateCard.css"
import {withStyles} from "@material-ui/core/styles";
import CheckCircleIcon from "@material-ui/icons/CheckCircle";
import {green} from "@material-ui/core/colors";
import {CardHeader} from "@material-ui/core";

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

const templateCardHeaderStyles = ({ palette, breakpoints }) => {
    const space = 24;
    return {
        root: {
            minWidth: 256,
        },
        header: {
            padding: `4px ${space}px 0`,
            display: 'flex',
            alignItems: 'center',
            flexDirection: 'row',
            justifyContent: 'space-between',
        },
    };
};

const TemplateCardHeader = withStyles(templateCardHeaderStyles, {
    name: 'TemplateCardHeader',
})(({ classes, selected, allowPreview = false, onPreview }) => (
    <div className={classes.root}>
        <div className={classes.header}>
            {allowPreview &&
            <IconButton
                onClick={onPreview}
                aria-label="show more">
                <VisibilityIcon/>
            </IconButton>
            }
            {selected && (<CheckCircleIcon style={{ color: green[500]}}>checkmark-image</CheckCircleIcon>)}
        </div>
    </div>
));

const TemplateCard = (props) => {

    const handleClick = (e) => {
        e.stopPropagation();
        props.onClick(e);
    }

    return (
        <Card onClick={props.onCardClick} className='feedback-template-card'>
            <CardHeader component={TemplateCardHeader} selected={props.selected} allowPreview={!props.isAdHoc} onPreview={handleClick}/>
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