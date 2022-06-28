package dev.mrflyn.replayaddon.spigui.pagination;


import dev.mrflyn.replayaddon.spigui.SGMenu;
import dev.mrflyn.replayaddon.spigui.buttons.SGButton;

public interface SGPaginationButtonBuilder {

    SGButton buildPaginationButton(SGPaginationButtonType type, SGMenu inventory);

}
