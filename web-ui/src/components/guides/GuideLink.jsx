import React from 'react';
import {Link} from 'react-router-dom';
import {ListItem, ListItemButton, ListItemText} from "@mui/material";
import PropTypes from 'prop-types';

import './GuideLink.css';

const propTypes = {
    props: PropTypes.shape({
        id: PropTypes.string.isRequired,
        name: PropTypes.string.isRequired,
        url: PropTypes.string.isRequired,
        description: PropTypes.string,
    })
};

const GuideLink = props => {
    return (
        <ListItemButton
            key={`doc-${props.id}`}
            to={props.url}
            component={Link}
            target="_blank"
        >
            <ListItem>
                <ListItemText primary={props.name} secondary={props.description}/>
            </ListItem>
        </ListItemButton>
    );
};

GuideLink.propTypes = propTypes;
export default GuideLink;
