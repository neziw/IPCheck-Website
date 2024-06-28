/*
 * This file is part of "IPCheck-Website", licensed under MIT License.
 *
 *  Copyright (c) 2024 neziw
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */
package ovh.neziw.ipcheck;

import io.javalin.Javalin;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public final class IPCheckApplication {

    public static void main(final String[] args) {
        int port = 8080;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException exception) {
                System.err.println("Port must be a number, defaulting to " + port);
            }
        }

        final Javalin javalin = Javalin.create(config -> config.staticFiles.add("/public"));
        javalin.start(port);

        javalin.get("/", ctx -> {
            final String htmlContent = readResource();
            ctx.html(htmlContent.replace("{{ip_address}}", ctx.ip()));
        });
        javalin.error(404, ctx -> ctx.redirect("/"));

        Runtime.getRuntime().addShutdownHook(new Thread(javalin::stop, "IPCheck-Shutdown-Hook"));
    }

    private static String readResource() {
        final InputStream inputStream = IPCheckApplication.class.getResourceAsStream("/public/index.html");
        if (inputStream != null) {
            try (final Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8)) {
                return scanner.useDelimiter("\\A").next();
            }
        }
        return "";
    }
}