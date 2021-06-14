import React from "react";
import PropTypes from "prop-types";
import Card from '@material-ui/core/Card';
import CardContent from '@material-ui/core/CardContent';

import "./TemplateCard.css"

const propTypes = {
    title: PropTypes.string.isRequired,
    description: PropTypes.string,
    creator: PropTypes.string.isRequired
}

const cutText = (text, maxCharacters) => {
    let shortenedText = text;
    if (text.length > maxCharacters) {
        shortenedText = `${text.substring(0, maxCharacters)}...`;
    }
    return shortenedText;
}

const TemplateCard = (props) => {
    return (
        <Card className = 'feedback-template-card'>
            <CardContent>
                <div className='card-content'>
                    <div>
                        <div className='template-name'>
                            {cutText(props.title, 20)}
                        </div>
                        <div className='description-and-creator'>
                            <div className='description'>
                                {cutText(props.description, 90)}
                            </div>
                        </div>
                    </div>
                    <div className='creator-wrapper'>
                        Created by:
                        <div className='creator'>
                            {props.creator}
                        </div>
                    </div>
               </div>
            </CardContent>
        </Card>
    );

}

TemplateCard.propTypes = propTypes;

export default TemplateCard;