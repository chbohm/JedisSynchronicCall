$Id: readme.txt 48743 2009-02-04 01:03:05Z adean $

The files in this directory are Axioma standard configurations.


ECLIPSE FORMATTER
   1. Load the Axioma standard configuration for the Eclipse formatter, found in
         http://subversion/svn/CodeStandards/JavaCodeStandards/trunk/

   2. Run the formatter ( when you are finished editing, before committing), to keep 
      our code looking consistent across all files and projects.

ECLIPSE CLEANUP
   1. Load the Axioma standard configuration for the Eclipse cleanup, found in
         http://subversion/svn/CodeStandards/JavaCodeStandards/trunk/

   2. Run the cleaup ( when you are finished editing, before committing), to keep 
      our code looking consistent across all files and projects.
  

FINDBUGS
I can't find a way to import/export Findbugs configuration to Eclipse, so for now
please follow these instructions:

   1. Install the findbugs plugin for Eclipse
         http://findbugs.cs.umd.edu/eclipse/
         http://findbugs.sourceforge.net/manual/eclipse.html 

   2. In Eclipse run the findbugs analysis on your project by clicking
      on the right mouse and selecting Findbugs from the context menu in Package Explorer.

   3. Browse the findbugs warning using the Findbugs perspective.

   4. There are some additional Findbugs features available if you install the stand-alone
      version of Findbugs:
         http://findbugs.sourceforge.net/




PMD/CPD

   1. Install the PMD plugin for Eclipse
      http://pmd.sourceforge.net/

   2. In Eclipse, look for duplicate code by right clicking on your project in the Package Explorer
      and selecting "PMD | Find Suspect Cut and Paste..."

   3. Refactor code to remove unnecessary duplication.

   4. Load the Axioma standard PMD configuration for Eclipse, available from
         http://subversion/svn/CodeStandards/JavaCodeStandards/trunk/

   5. In Eclipse, find PMD violations by right clicking on your project in the Package Explorer
      and selecting "PMD | Check code with PMD"

   6. Browse the PMD warnings using the PMD perspective.






CHECKSTYLE
   1. Install the Checkstyle plugin for Eclipse
         http://eclipse-cs.sourceforge.net/update

   2. In Eclipse, find Checkstyle violations by right clicking on your project in the Package Explorer
      and selecting "Checkstyle | Check code with Checkstyle"



ECLIPSE TPTP ANALYZER
   The Eclipse TPTP project includes a static analysis component.  See
      http://planeteclipse.net/tptp/home/documents/process/development/static_analysis/TPTP_static_analysis_tutorial_part1.html
   for details.