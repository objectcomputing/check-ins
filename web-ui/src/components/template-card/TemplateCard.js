import React from "react";
import "./TemplateCard.css"
import Card from '@material-ui/core/Card';
//import CardActions from '@material-ui/core/CardActions';
import CardContent from '@material-ui/core/CardContent';

const templateCard = () => {
    const templateName = "Ad Hoc"
    const description = "Ask a single question"
    const creator = "Admin"

    return (
        <Card className = 'card'>
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
        </Card>
    );

}

export default templateCard;