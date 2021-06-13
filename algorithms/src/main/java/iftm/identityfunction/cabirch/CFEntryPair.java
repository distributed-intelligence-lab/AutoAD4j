/*
 *  This file is part of JBIRCH.
 *
 *  JBIRCH is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  JBIRCH is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with JBIRCH.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

 /*
 *  CFEntryPair.java
 *  Copyright (C) 2009 Roberto Perdisci (roberto.perdisci@gmail.com)
 */
package iftm.identityfunction.cabirch;

import java.util.Objects;

/**
 *
 * @author Roberto Perdisci (roberto.perdisci@gmail.com)
 *
 */
public class CFEntryPair {

    private static final String LINE_SEP = System.getProperty("line.separator");

    private CFEntry e1;
    private CFEntry e2;

    public CFEntryPair() {
    }

    public CFEntryPair(CFEntry e1, CFEntry e2) {
        this.e1 = e1;
        this.e2 = e2;
    }

    @Override
    public boolean equals(Object o) {

        if(o == null){
            return false;
        }

        if (this.getClass() != o.getClass())
            return false;

        CFEntryPair p = (CFEntryPair) o;

        if (e1.equals(p.e1) && e2.equals(p.e2)) {
            return true;
        }

        if (e1.equals(p.e2) && e2.equals(p.e1)) {
            return true;
        }

        return false;
    }

    @Override
    public String toString() {
        StringBuilder buff = new StringBuilder();

        buff.append("---- CFEntryPiar ----").append(LINE_SEP);
        buff.append("---- e1 ----").append(LINE_SEP);
        buff.append(e1.toString()).append(LINE_SEP);
        buff.append("---- e2 ----").append(LINE_SEP);
        buff.append(e2.toString()).append(LINE_SEP);
        buff.append("-------- end --------").append(LINE_SEP);

        return buff.toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(e1, e2);
    }

    public CFEntry getE1() {
        return e1;
    }

    public CFEntry getE2() {
        return e2;
    }

    public void setE1(CFEntry e1) {
        this.e1 = e1;
    }

    public void setE2(CFEntry e2) {
        this.e2 = e2;
    }
}
