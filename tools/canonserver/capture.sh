#!/bin/bash
#
# This file is part of VisiCut.
# Copyright (C) 2011 Thomas Oster <thomas.oster@rwth-aachen.de>
# RWTH Aachen University - 52062 Aachen, Germany
# 
#     VisiCut is free software: you can redistribute it and/or modify
#     it under the terms of the GNU Lesser General Public License as published by
#     the Free Software Foundation, either version 3 of the License, or
#     (at your option) any later version.
# 
#    VisiCut is distributed in the hope that it will be useful,
#     but WITHOUT ANY WARRANTY; without even the implied warranty of
#     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#     GNU Lesser General Public License for more details.
# 
#     You should have received a copy of the GNU Lesser General Public License
#     along with VisiCut.  If not, see <http://www.gnu.org/licenses/>.
#
gphoto2 --capture-image-and-download >/dev/null 2>&1
	echo -ne "HTTP/1.0 200 OK\r\n"
	echo -ne "Content-Type: image/jpeg\r\n\r\n"
	cat capt0000.jpg
	rm capt0000.jpg
