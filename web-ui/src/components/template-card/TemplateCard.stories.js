import React from "react";
import TemplateCard from "./TemplateCard";

export default {
  title: "Check Ins/TemplateCard",
  component: TemplateCard
};

const Template = (args) => <TemplateCard {...args} />;

export const TemplateCardDefault = Template.bind({});
TemplateCardDefault.args = {
  title: "",
  description: "",
  creator: ""
};

export const SetTemplateCard = Template.bind({});
SetTemplateCard.args = {
  title: "Template",
  description: "An example feedback template",
  creator: "Admin"
};