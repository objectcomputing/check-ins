import React from "react";
import "./TemplateCard.css"
//import { makeStyles } from '@material-ui/core/styles';
import Card from '@material-ui/core/Card';
//import CardActions from '@material-ui/core/CardActions';
import CardContent from '@material-ui/core/CardContent';
//import Button from '@material-ui/core/Button';
import Typography from '@material-ui/core/Typography';
const templateCard = () => {
    const templateName = "Ad Hoc"
    const description = "Ask a single question"
    const creator = "Admin"

//        const bull = <span className='bullet'>•</span>;

        return (
            <Card className = 'root'>
                <CardContent>
                    <Typography className='templateName' >
                        {templateName}
                    </Typography>
                    <Typography className='description'>
                        {description}
                    </Typography>
                    <Typography className='creator'>
                        {creator}
                    </Typography>
                </CardContent>
            </Card>
        );

}

export default templateCard;