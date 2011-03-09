package org.jboss.as.console.client.widgets.forms;

import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

import java.util.Map;

/**
 * The default renderer for a group of form items.
 *
 * @see Form
 *
 * @author Heiko Braun
 * @date 3/3/11
 */
class DefaultGroupRenderer implements GroupRenderer
{
    private final String id = "form-"+ HTMLPanel.createUniqueId()+"_";
    private final String tablePrefix = "<table border=0 id='"+id+"' border=0 cellpadding=0 cellspacing=0>";
    private final static String tableSuffix = "</table>";

    DefaultGroupRenderer() {

    }

    @Override
    public Widget render(RenderMetaData metaData, String groupName, Map<String, FormItem> groupItems)
    {

        SafeHtmlBuilder builder = new SafeHtmlBuilder();
        builder.appendHtmlConstant(tablePrefix);

        // build html structure
        FormItem[] values = groupItems.values().toArray(new FormItem[0]);
        int i=0;
        while(i<values.length)
        {
            builder.appendHtmlConstant("<tr>");

            int col=0;
            for(col=0; col<metaData.getNumColumns(); col++)
            {
                int next = i + col;
                if(next<values.length)
                {
                    FormItem item = values[next];
                    createItemCell(metaData, builder, item);
                }
                else
                {
                    break;
                }
            }

            builder.appendHtmlConstant("</tr>");
            i+=col;
        }

        builder.appendHtmlConstant(tableSuffix);

        HTMLPanel panel = new HTMLPanel(builder.toSafeHtml());

        // inline widget
        for(FormItem item : groupItems.values())
        {
            final String widgetId = id + item.getName();
            panel.add(item.asWidget(), widgetId);

        }

        return panel;
    }

    private void createItemCell(RenderMetaData metaData, SafeHtmlBuilder builder, FormItem item) {

        final String widgetId = id + item.getName();

        builder.appendHtmlConstant("<td class='form-item-title' style='min-width:"+metaData.getTitleWidth()*5+"pt'>");
        builder.appendEscaped(item.getTitle()+":");
        builder.appendHtmlConstant("</td>");

        builder.appendHtmlConstant("<td id='" + widgetId + "' class='form-item'>").appendHtmlConstant("</td>");
        // contents added later
        builder.appendHtmlConstant("</td>");
    }
}