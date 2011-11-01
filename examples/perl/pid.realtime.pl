#!/usr/bin/perl

use vars qw/$libpath/;
use FindBin qw($Bin);
BEGIN { $libpath="$Bin" };
use lib "$libpath";
use lib "$libpath/../lib";

$bin = "/usr/bin";
$dirlogs = "$Bin/status";
$dirpid = $dirlogs;
mkdir $dirlogs unless (-e $dirlogs);
$templatedir = "$Bin/templates";
die "Can't find templates" unless (-e $templatedir);
$logdir = "$Bin/logs";
mkdir $logdir unless (-e $logdir);

#my $addparam = "-b ";
my ($xmlbasic, $xmlimage) = loadtemplates($templatedir);
my $org = "10622";
my $authkey = "199b2ba6-83fb-4992-a382-f912452b3f84";

$pidsource = $ARGV[0];
if ($pidsource=~/^\d+$/)
{
    $pid = $pidsource;
    my $PID = pidrequest($pid);
    print "$pid => $PID\n";
}
else
{
    exit(0) unless (-e $pidsource);
    open(pids, "$pidsource");
    @pids = <pids>;
    close(pids);

    foreach $pidstr (@pids)
    {
	$pidstr=~s/\r|\n//g;
	my ($pid, $barcode) = split(/\;\;/, $pidstr);
        $true = 1;

	if ($true)
	{
            $urls{$pid} = "http://search.socialhistoryservices.org/Record/$pid";
            $pidfile = filltemplate("$xmlbasic", $dirpid, $pid);

	    if (-e "$dirpid/$pidfile")
	    {
	        $counter++;
	        print "#$counter\n";
                createpidrequest($pidfile, $dirlogs, $addparam);
	    }
	    else
	    {
		print "Can't find $pidfile\n";
	    };
        }
    };
}

sub pidrequest
{
    my ($pid, $DEBUG) = @_;
    my $PID;

    if ($pid)
    {
            $urls{$pid} = "http://search.socialhistoryservices.org/Record/$pid";
            $pidfile = filltemplate("$xmlbasic", $dirpid, $pid);

            if (-e "$dirpid/$pidfile")
            {
                $counter++;
                $PID = createpidrequest($pidfile, $dirlogs, $addparam);
            }
            else
            {
                print "Can't find $pidfile\n";
            };
    };

    return $PID;
}

sub createpidrequest
{
    my ($pidfile, $outdir, $addparam, $DEBUG) = @_;
    my $PID;

    my $statusfile = "$pidfile.pid";

    my $postfile = "$dirpid/$pidfile";

    my $startdir = "0000";
    if ($pidfile=~/^(\d{4})/)
    {
	$startdir = $1;
    }

    $statusfile = "$outdir/$statusfile" if ($outdir);

    $command = "$bin/wget $addparam --header=\"Content-Type: text/xml\" --header=\"Authorization: oauth $authkey\" https://pid.socialhistoryservices.org/secure --no-check-certificate --post-file=$postfile -O $statusfile -a $logdir/pid.log";
    print "$command\n" if ($DEBUG);
    $exe = `$command`;

    $soap = `/bin/cat $statusfile`;
#   <SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/"><SOAP-ENV:Header/><SOAP-ENV:Body><ns2:CreatePidResponse xmlns:ns2="http://pid.socialhistoryservices.org/"><ns2:handle><ns2:pid>10622/54F60846-49C1
#-4ED9-A7EA-09418D6B8FE4</ns2:pid><ns2:resolveUrl>http://search.socialhistoryservices.org/Record/312</ns2:resolveUrl></ns2:handle></ns2:CreatePidResponse></SOAP-ENV:Body></SOAP-ENV:Envelope>
    if ($soap=~/<ns2\:pid>(.+?)<\/ns2\:pid>/i)
    {
       $PID = $1;
    }
    unlink $statusfile if ($PID);

    return $PID;
};

sub loadtemplates
{
    my ($templatedir, $DEBUG) = @_;

    open(template1, "$templatedir/soap_basic.xml");
    @xmlbasic = <template1>;
    close(template1);

    open(template2, "$templatedir/soap_image.xml");
    @xmlimage = <template2>;
    close(template2);

    return ("@xmlbasic", "@xmlimage");
}

sub filltemplate
{
    my ($template, $dirpid, $pid, $DEBUG) = @_;

    $template=~s/\%\%org\%\%/$org/sxgi;
    $template=~s/\%\%url\%\%/$urls{$pid}/sxgi;
    $template=~s/\%\%barcode\%\%/$barcode{$pid}/sxgi;

    my $pidfile = "$pid.xml";
    open(fileout, ">$dirpid/$pidfile");
    print fileout "$template\n";
    close(fileout);

    return $pidfile;
}
