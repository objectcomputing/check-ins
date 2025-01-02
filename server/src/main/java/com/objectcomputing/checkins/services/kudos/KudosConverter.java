package com.objectcomputing.checkins.services.kudos;

import com.objectcomputing.checkins.notifications.social_media.SlackPoster;
import com.objectcomputing.checkins.notifications.social_media.SlackSearch;
import com.objectcomputing.checkins.services.kudos.kudos_recipient.KudosRecipientServices;
import com.objectcomputing.checkins.services.kudos.kudos_recipient.KudosRecipient;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileUtils;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;

import com.slack.api.model.block.LayoutBlock;
import com.slack.api.model.block.RichTextBlock;
import com.slack.api.model.block.element.RichTextElement;
import com.slack.api.model.block.element.RichTextSectionElement;
import com.slack.api.util.json.GsonFactory;
import com.google.gson.Gson;
import io.micronaut.core.annotation.Introspected;
import jakarta.inject.Singleton;

import java.util.UUID;
import java.util.List;
import java.util.ArrayList;

@Singleton
public class KudosConverter {
    private record InternalBlock(
      List<LayoutBlock> blocks
    ) {}

    private final MemberProfileServices memberProfileServices;
    private final KudosRecipientServices kudosRecipientServices;
    private final SlackSearch slackSearch;

    public KudosConverter(MemberProfileServices memberProfileServices,
                          KudosRecipientServices kudosRecipientServices,
                          SlackSearch slackSearch) {
      this.memberProfileServices = memberProfileServices;
      this.kudosRecipientServices = kudosRecipientServices;
      this.slackSearch = slackSearch;
    }

    public String toSlackBlock(Kudos kudos) {
        // Build the message text out of the Kudos data.
        List<RichTextElement> content = new ArrayList<>();

        // Look up the channel id from Slack
        String channelName = "kudos";
        String channelId = slackSearch.findChannelId(channelName);
        if (channelId == null) {
            content.add(
                RichTextSectionElement.Text.builder()
                    .text("#" + channelName)
                    .style(boldItalic())
                    .build()
            );
        } else {
            content.add(
                RichTextSectionElement.Channel.builder()
                    .channelId(channelId)
                    .style(limitedBoldItalic())
                    .build()
            );
        }
        content.add(
            RichTextSectionElement.Text.builder()
                .text(" from ")
                .style(boldItalic())
                .build()
        );
        content.add(memberAsRichText(kudos.getSenderId()));
        content.addAll(recipients(kudos));

        content.add(
            RichTextSectionElement.Text.builder()
                .text("\n" + kudos.getMessage() + "\n")
                .style(boldItalic())
                .build()
        );

        // Bring it all together.
        RichTextSectionElement element = RichTextSectionElement.builder()
            .elements(content).build();
        RichTextBlock richTextBlock = RichTextBlock.builder()
            .elements(List.of(element)).build();
        InternalBlock block = new InternalBlock(List.of(richTextBlock));
        Gson mapper = GsonFactory.createSnakeCase();
        return mapper.toJson(block);
    }

    private RichTextSectionElement.TextStyle boldItalic() {
        return RichTextSectionElement.TextStyle.builder()
                   .bold(true).italic(true).build();
    }

    private RichTextSectionElement.LimitedTextStyle limitedBoldItalic() {
        return RichTextSectionElement.LimitedTextStyle.builder()
                   .bold(true).italic(true).build();
    }

    private RichTextElement memberAsRichText(UUID memberId) {
        // Look up the user id by email address on Slack
        MemberProfile profile = memberProfileServices.getById(memberId);
        String userId = slackSearch.findUserId(profile.getWorkEmail());
        if (userId == null) {
          String name = MemberProfileUtils.getFullName(profile);
          return RichTextSectionElement.Text.builder()
                     .text("@" + name)
                     .style(boldItalic())
                     .build();
        } else {
          return RichTextSectionElement.User.builder()
                     .userId(userId)
                     .style(limitedBoldItalic())
                     .build();
        }
    }

    private List<RichTextElement> recipients(Kudos kudos) {
        List<RichTextElement> list = new ArrayList<>();
        List<KudosRecipient> recipients =
            kudosRecipientServices.getAllByKudosId(kudos.getId());
        String separator = " to ";
        for (KudosRecipient recipient : recipients) {
            list.add(RichTextSectionElement.Text.builder()
                         .text(separator)
                         .style(boldItalic())
                         .build());
            list.add(memberAsRichText(recipient.getMemberId()));
            separator = ", ";
        }
        return list;
    }
}

