import React from "react";

import { ResponsiveRadar } from "@nivo/radar";

const MyResponsiveRadar = ({ data, selectedMembers }) => (
  <ResponsiveRadar
    data={data}
    keys={selectedMembers.map((member) => member.name)}
    indexBy="skill"
    maxValue="auto"
    margin={{ top: 70, right: 80, bottom: 40, left: 80 }}
    curve="linearClosed"
    borderWidth={2}
    borderColor={{ from: "color" }}
    gridLevels={5}
    gridShape="circular"
    gridLabelOffset={36}
    enableDots={true}
    dotSize={10}
    dotColor={{ theme: "background" }}
    dotBorderWidth={2}
    dotBorderColor={{ from: "color" }}
    enableDotLabel={true}
    dotLabel="value"
    dotLabelYOffset={-12}
    colors={{ scheme: "nivo" }}
    fillOpacity={0.25}
    blendMode="multiply"
    animate={true}
    motionConfig="wobbly"
    isInteractive={true}
    legends={[
      {
        anchor: "top-left",
        direction: "column",
        translateX: -50,
        translateY: -40,
        itemWidth: 80,
        itemHeight: 20,
        itemTextColor: "#999",
        symbolSize: 12,
        symbolShape: "circle",
        effects: [
          {
            on: "hover",
            style: {
              itemTextColor: "#000",
            },
          },
        ],
      },
    ]}
  />
);

export default MyResponsiveRadar;
