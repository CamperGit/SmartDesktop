package com.camper.SmartDesktop;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

public class NodeDragger
{
    private static double xUndo=80;
    private static double yUndo=30;

    public static void doDragging(Node selected, MouseEvent event)
    {
        //Сделать проверку, чтобы не наезжало на панель инструментов!!!
        if (xUndo==80 && yUndo==30)
        {
            if (event.getX()>=80 && event.getY()>=30)
            {
                xUndo=event.getX();
                yUndo=event.getY();
            }
        }
        selected.setLayoutX((int)event.getX()-((int)xUndo-selected.getLayoutX()));
        selected.setLayoutY((int)event.getY()-((int)yUndo-selected.getLayoutY()));
    }
}
