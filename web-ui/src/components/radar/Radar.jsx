import React from "react";

import { Radar, RadarChart, PolarGrid, Legend, PolarAngleAxis, PolarRadiusAxis, ResponsiveContainer } from 'recharts';



const MyRadar = ({ data, selectedMembers }) => (

        <ResponsiveContainer width="100%" height="100%">
        <RadarChart cx="50%" cy="50%" outerRadius="80%" data={data}>
            <PolarGrid />
            <PolarAngleAxis dataKey="skill" />
            <PolarRadiusAxis domain={[0,5]}/>
            {selectedMembers.map((member) => (
                <Radar
                    name={member.name}
                    dataKey={member.name}
                    fill={member.color}
                    fillOpacity={0.6}
                />
            ))}
            <Legend />
        </RadarChart>
    </ResponsiveContainer>
)


//So this works pretty well but two problems: the color randomizes every time you hit search. It would be better to assign a color for a member and just pass that through.
//Also when fewer than 2 skills or 2 people are selected, the radar chart doesn't render.
// It would be better to show a message like "Please select at least 2 people and 2 skills to generate a radar chart."
//Or create some other kind of chart
export default MyRadar;

