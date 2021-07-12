import "./TemplateCard.css";
import React, {useContext, useEffect, useState} from "react";
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
import {AppContext} from "../../context/AppContext";
import {selectCsrfToken} from "../../context/selectors";
import {getMember} from "../../api/member";

const propTypes = {
    title: PropTypes.string.isRequired,
    description: PropTypes.string,
    createdBy: PropTypes.string.isRequired,
    isAdHoc: PropTypes.bool,
    onPreviewClick: PropTypes.func,
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
    const space = 8;
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

    const { state } = useContext(AppContext);
    const csrf = selectCsrfToken(state);
    const [creatorName, setCreatorName] = useState("");

    const handlePreviewClick = (e) => {
        e.stopPropagation();
        props.onPreviewClick(e);
    }

    // Get name of the template creator
    useEffect(() => {
        async function getCreatorName() {
            if (props.createdBy) {
                let res = await getMember(props.createdBy, csrf);
                let creatorProfile =
                  res.payload && res.payload.data && !res.error
                    ? res.payload.data
                    : null
                setCreatorName(creatorProfile ? creatorProfile.name : "");
            }
        }
        if (csrf) {
            getCreatorName();
        }
    }, [props.createdBy, csrf]);

    return (
        <Card onClick={props.onCardClick} className='feedback-template-card'>
            <CardHeader
              component={TemplateCardHeader}
              selected={props.selected}
              allowPreview
              onPreview={handlePreviewClick}/>
            <CardContent className="card-content">
                <div className="template-details">
                    <h3 className="template-name">{cutText(props.title, 20)}</h3>
                    <p className="description">{cutText(props.description, 90)}</p>
                </div>
                <p className="creator">Created by: <b>{creatorName}</b></p>
            </CardContent>
        </Card>
    );

};

TemplateCard.propTypes = propTypes;

export default TemplateCard;