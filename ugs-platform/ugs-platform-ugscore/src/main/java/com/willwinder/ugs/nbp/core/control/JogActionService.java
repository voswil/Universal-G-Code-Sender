/*
    Copywrite 2016 Will Winder

    This file is part of Universal Gcode Sender (UGS).

    UGS is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    UGS is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with UGS.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.willwinder.ugs.nbp.core.control;

import static com.willwinder.ugs.nbp.core.control.JogActionService.Operation.*;
import com.willwinder.ugs.nbp.lib.services.ActionRegistrationService;
import com.willwinder.ugs.nbp.lib.lookup.CentralLookup;
import com.willwinder.universalgcodesender.i18n.Localization;
import com.willwinder.universalgcodesender.model.UnitUtils.Units;
import com.willwinder.universalgcodesender.services.JogService;
import java.awt.event.ActionEvent;
import java.io.IOException;
import javax.swing.AbstractAction;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author wwinder
 */
@ServiceProvider(service=JogActionService.class) 
public class JogActionService {
    private JogService jogService;

    public JogActionService() {
        jogService = CentralLookup.getDefault().lookup(JogService.class);
        initActions();
    }

    /**
     * Create the actions, this makes them available for keymapping and makes
     * them usable from the drop down menu's.
     */
    private void initActions() {
        ActionRegistrationService ars =  Lookup.getDefault().lookup(ActionRegistrationService.class);

        try {
            String localized = String.format("Menu/%s/%s",
                    Localization.getString("platform.menu.machine"),
                    Localization.getString("platform.menu.jog"));
            String category = "Machine";
            String localCategory = Localization.getString("platform.menu.machine");
            String menuPath = "Menu/" + category + "/Jog";
            
            ars.registerAction(JogActionService.class.getCanonicalName() + ".xPlus", Localization.getString("jogging.xPlus") ,
                    category, localCategory, "M-RIGHT" , menuPath, localized, new JogAction(jogService, 1, 0));
            ars.registerAction(JogActionService.class.getCanonicalName() + ".xMinus", Localization.getString("jogging.xMinus"),
                    category, localCategory, "M-LEFT"  , menuPath, localized, new JogAction(jogService,-1, 0));
            ars.registerAction(JogActionService.class.getCanonicalName() + ".yPlus", Localization.getString("jogging.yPlus") ,
                    category, localCategory, "M-UP"    , menuPath, localized, new JogAction(jogService, 0, 1));
            ars.registerAction(JogActionService.class.getCanonicalName() + ".yMinus", Localization.getString("jogging.yMinus"),
                    category, localCategory, "M-DOWN"  , menuPath, localized, new JogAction(jogService, 0,-1));
            ars.registerAction(JogActionService.class.getCanonicalName() + ".zPlus", Localization.getString("jogging.zPlus") ,
                    category, localCategory, "SM-UP"   , menuPath, localized, new JogAction(jogService, 1));
            ars.registerAction(JogActionService.class.getCanonicalName() + ".zMinus", Localization.getString("jogging.zMinus"),
                    category, localCategory, "SM-DOWN" , menuPath, localized, new JogAction(jogService, -1));

            localized = String.format("Menu/%s/%s/%s",
                    Localization.getString("platform.menu.machine"),
                    Localization.getString("platform.menu.jog"),
                    Localization.getString("platform.menu.jog.size"));
            menuPath = menuPath + "/Step Size";

            // Set Step Size XY
            ars.registerAction(JogSizeAction.class.getCanonicalName() + "xy.10", "XY 10",
                    category, localCategory, "" , menuPath, localized, new JogSizeAction(jogService, 10, true));
            ars.registerAction(JogSizeAction.class.getCanonicalName() + "xy.1", "XY 1",
                    category, localCategory, "" , menuPath, localized, new JogSizeAction(jogService, 1, true));
            ars.registerAction(JogSizeAction.class.getCanonicalName() + "xy.01", "XY 0.1",
                    category, localCategory, "" , menuPath, localized, new JogSizeAction(jogService, 0.1, true));
            ars.registerAction(JogSizeAction.class.getCanonicalName() + "xy.001", "XY 0.01",
                    category, localCategory, "" , menuPath, localized, new JogSizeAction(jogService, 0.01, true));
            ars.registerAction(JogSizeAction.class.getCanonicalName() + "xy.0001", "XY 0.001",
                    category, localCategory, "" , menuPath, localized, new JogSizeAction(jogService, 0.001, true));

            // Set Step Size Z
            ars.registerAction(JogSizeAction.class.getCanonicalName() + "z.10", "Z 10",
                    category, localCategory, "" , menuPath, localized, new JogSizeAction(jogService, 10, false));
            ars.registerAction(JogSizeAction.class.getCanonicalName() + "z.1", "Z 1",
                    category, localCategory, "" , menuPath, localized, new JogSizeAction(jogService, 1, false));
            ars.registerAction(JogSizeAction.class.getCanonicalName() + "z.01", "Z 0.1",
                    category, localCategory, "" , menuPath, localized, new JogSizeAction(jogService, 0.1, false));
            ars.registerAction(JogSizeAction.class.getCanonicalName() + "z.001", "Z 0.01",
                    category, localCategory, "" , menuPath, localized, new JogSizeAction(jogService, 0.01, false));
            ars.registerAction(JogSizeAction.class.getCanonicalName() + "z.0001", "Z 0.001",
                    category, localCategory, "" , menuPath, localized, new JogSizeAction(jogService, 0.001, false));

            // Step Size XY
            ars.registerAction(JogSizeAction.class.getCanonicalName() + ".divide", Localization.getString("jogging.divide"),
                    category, localCategory, "" , menuPath, localized, new JogSizeAction(jogService, STEPXY_DIVIDE));
            ars.registerAction(JogSizeAction.class.getCanonicalName() + ".multiply", Localization.getString("jogging.multiply"),
                    category, localCategory, "" , menuPath, localized, new JogSizeAction(jogService, STEPXY_MULTIPLY));
            ars.registerAction(JogSizeAction.class.getCanonicalName() + ".decrease", Localization.getString("jogging.decrease"),
                    category, localCategory, "" , menuPath, localized, new JogSizeAction(jogService, STEPXY_MINUS));
            ars.registerAction(JogSizeAction.class.getCanonicalName() + ".increase", Localization.getString("jogging.increase"),
                    category, localCategory, "" , menuPath, localized, new JogSizeAction(jogService, STEPXY_PLUS));

            // Step Size Z
            ars.registerAction(JogSizeAction.class.getCanonicalName() + ".divide.z", Localization.getString("jogging.divide.z"),
                    category, localCategory, "" , menuPath, localized, new JogSizeAction(jogService, STEPZ_DIVIDE));
            ars.registerAction(JogSizeAction.class.getCanonicalName() + ".multiply.z", Localization.getString("jogging.multiply.z"),
                    category, localCategory, "" , menuPath, localized, new JogSizeAction(jogService, STEPZ_MULTIPLY));
            ars.registerAction(JogSizeAction.class.getCanonicalName() + ".decrease.z", Localization.getString("jogging.decrease.z"),
                    category, localCategory, "" , menuPath, localized, new JogSizeAction(jogService, STEPZ_MINUS));
            ars.registerAction(JogSizeAction.class.getCanonicalName() + ".increase.z", Localization.getString("jogging.increase.z"),
                    category, localCategory, "" , menuPath, localized, new JogSizeAction(jogService, STEPZ_PLUS));

            // Feed Rate
            ars.registerAction(JogSizeAction.class.getCanonicalName() + ".decrease.feed", Localization.getString("jogging.decrease.feed"),
                    category, localCategory, "" , menuPath, localized, new JogSizeAction(jogService, FEED_MINUS));
            ars.registerAction(JogSizeAction.class.getCanonicalName() + ".increase.feed", Localization.getString("jogging.increase.feed"),
                    category, localCategory, "" , menuPath, localized, new JogSizeAction(jogService, FEED_PLUS));

            // Units
            ars.registerAction(JogSizeAction.class.getCanonicalName() + ".inch", Localization.getString("mainWindow.swing.inchRadioButton"),
                    category, localCategory, "" , menuPath, localized, new JogSizeAction(jogService, Units.INCH));
            ars.registerAction(JogSizeAction.class.getCanonicalName() + ".mm", Localization.getString("mainWindow.swing.mmRadioButton"),
                    category, localCategory, "" , menuPath, localized, new JogSizeAction(jogService, Units.MM));

        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    protected enum Operation {
      STEPXY_PLUS,
      STEPXY_MINUS,
      STEPXY_MULTIPLY,
      STEPXY_DIVIDE,
      STEPZ_PLUS,
      STEPZ_MINUS,
      STEPZ_MULTIPLY,
      STEPZ_DIVIDE,
      FEED_PLUS,
      FEED_MINUS,
      FEED_MULTIPLY,
      FEED_DIVIDE;
    }

    protected class JogSizeAction extends AbstractAction {
        private JogService js;
        private Double size = null;
        private Operation operation = null;
        private Units unit = null;
        private Boolean xy = null;

        public JogSizeAction(JogService service, Units u) {
            js = service;
            unit = u;
        }
        public JogSizeAction(JogService service, Operation op) {
            js = service;
            operation = op;
        }

        public JogSizeAction(JogService service, double size, boolean xy) {
            js = service;
            this.size = size;
            this.xy = xy;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (size != null) {
                if (xy) {
                    js.setStepSizeXY(size);
                } else {
                    js.setStepSizeZ(size);
                }
            }
            else if (operation != null) {
                switch (operation) {
                    case STEPXY_MULTIPLY:
                        js.multiplyXYStepSize();
                        break;
                    case STEPXY_DIVIDE:
                        js.divideXYStepSize();
                        break;
                    case STEPXY_PLUS:
                        js.increaseXYStepSize();
                        break;
                    case STEPXY_MINUS:
                        js.decreaseXYStepSize();
                        break;
                    case STEPZ_MULTIPLY:
                        js.multiplyZStepSize();
                        break;
                    case STEPZ_DIVIDE:
                        js.divideZStepSize();
                        break;
                    case STEPZ_PLUS:
                        js.increaseZStepSize();
                        break;
                    case STEPZ_MINUS:
                        js.decreaseZStepSize();
                        break;
                    case FEED_PLUS:
                      js.increaseFeedRate();
                      break;
                    case FEED_MINUS:
                      js.decreaseFeedRate();
                      break;
                    default:
                        break;
                }
            } else if (unit != null) {
                js.setUnits(unit);
            }
        }

        @Override
        public boolean isEnabled() {
            return true;
        }
    }

    protected class JogAction extends AbstractAction {
        private JogService js;
        private int x,y,z;
        private boolean isZ;

        public JogAction(JogService service, int x, int y) {
            js = service;
            this.x = x;
            this.y = y;
            this.z = 0;
            this.isZ = false;
        }

        public JogAction(JogService service, int z) {
            js = service;
            this.x = 0;
            this.y = 0;
            this.z = z;
            this.isZ = true;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (isZ) {
                js.adjustManualLocationZ(z);
            } else {
                js.adjustManualLocationXY(x, y);
            }
        }

        @Override
        public boolean isEnabled() {
            return js.canJog();
        }
    }
}
